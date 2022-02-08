package com.xlc.spartacus.common.es.impl;

import com.xlc.spartacus.common.core.pojo.Page;
import com.xlc.spartacus.common.es.ElasticsearchService;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * es服务 实现
 *
 * @author xlc, since 2021
 */
@Component("elasticsearchService")
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchServiceImpl.class);

    @Resource
    private RestHighLevelClient client;


    @Override
    public boolean add(String index, String id, Map<String, Object> doc) throws Exception {
        IndexRequest request = new IndexRequest(index).id(id).source(doc);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

//        System.out.println("-------------");
//        System.out.println(JSONObject.toJSON(response));
//        System.out.println("-------------");

        DocWriteResponse.Result result = response.getResult();
        return result == DocWriteResponse.Result.CREATED || result == DocWriteResponse.Result.UPDATED;
    }

    @Override
    public boolean bulkAdd(String index, Map<String, Map<String, Object>> docs) throws Exception {
        BulkRequest request = new BulkRequest();
        for(Map.Entry<String, Map<String, Object>> en : docs.entrySet()) {
            request.add(new IndexRequest(index).id(en.getKey()).source(en.getValue()));
        }

        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        return !response.hasFailures();
    }

    @Override
    public boolean update(String index, String id, Map<String,Object> doc) throws Exception {
        UpdateRequest request = new UpdateRequest(index, id).doc(doc);

        request.docAsUpsert(true); // 如果文档不存在，则作为新文档插入

        UpdateResponse response = client.update(request,RequestOptions.DEFAULT);

        DocWriteResponse.Result result = response.getResult();
        return result == DocWriteResponse.Result.CREATED
                || result == DocWriteResponse.Result.UPDATED
                || result == DocWriteResponse.Result.NOOP;
    }

    @Override
    public boolean bulkUpdate(String index, Map<String, Map<String,Object>> docs) throws Exception {
        BulkRequest request = new BulkRequest();
        for(Map.Entry<String, Map<String, Object>> en : docs.entrySet()) {
            UpdateRequest updateRequest = new UpdateRequest(index, en.getKey()).doc(en.getValue());
            updateRequest.docAsUpsert(true); // 如果文档不存在，则作为新文档插入
            request.add(updateRequest);
        }

        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        return !response.hasFailures();
    }

    @Override
    public boolean delete(String index, String id) throws Exception {
        DeleteRequest request = new DeleteRequest(index, id);
        DeleteResponse response = client.delete(request,RequestOptions.DEFAULT);

        DocWriteResponse.Result result = response.getResult();
        return (result == DocWriteResponse.Result.DELETED || result == DocWriteResponse.Result.NOT_FOUND);
    }

    @Override
    public boolean bulkDelete(String index, List<String> ids) throws Exception {
        BulkRequest request = new BulkRequest();
        for(String id : ids) {
            request.add(new DeleteRequest(index, id));
        }

        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        return !response.hasFailures();
    }

    @Override
    public boolean deleteAll(String index) throws Exception {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(QueryBuilders.matchAllQuery());

        BulkByScrollResponse response = client.deleteByQuery(request,RequestOptions.DEFAULT);
        return response.getDeleted() == response.getTotal();
    }

    @Override
    public Map<String,Object> get(String index, String id) throws Exception {
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse response = client.get(getRequest,RequestOptions.DEFAULT);
        return response.getSource();
    }

    @Override
    public Page searchMatch(String index, String searchText, String matchFields, Map<String, Object> mustMatchs, Map<String, Map<String, Object>> rangeMatchs, String sortField, String highlightFields, int currentPage, int pageSize) throws Exception {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //匹配所有文档
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //不分词的字段 必须完全匹配
        if (mustMatchs != null && mustMatchs.size() > 0) {
            for(Map.Entry<String, Object> en : mustMatchs.entrySet()) {
                //下面两种都可
//                boolQueryBuilder.must(QueryBuilders.termQuery(en.getKey(), en.getValue()));
                boolQueryBuilder.filter(QueryBuilders.matchQuery(en.getKey(), en.getValue()).operator(Operator.AND));
            }
        }

        //要分词的字段 部分匹配即可
        if (StringUtils.isNotEmpty(searchText) && StringUtils.isNotEmpty(matchFields)) {
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchText, matchFields.split(",")).operator(Operator.OR));
        }

        //范围查询的字段
        if (rangeMatchs != null && rangeMatchs.size() > 0) {
            for(Map.Entry<String, Map<String, Object>> en : rangeMatchs.entrySet()) {
                Map<String, Object> rangeProps = en.getValue();
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(en.getKey()).gte(rangeProps.get("gte")).lte(rangeProps.get("lte"));
                if(rangeProps.containsKey("format") && StringUtils.isNotEmpty((String) rangeProps.get("format"))) {
                    rangeQueryBuilder.format((String) rangeProps.get("format"));
                }
                boolQueryBuilder.must(rangeQueryBuilder);
            }
        }

        searchSourceBuilder.query(boolQueryBuilder);

        //先按照score降序，再按照排序字段降序
        SortBuilder scoreSortBuilder = SortBuilders.scoreSort().order(SortOrder.DESC);
        SortBuilder fieldSortBuilder = SortBuilders.fieldSort(sortField).order(SortOrder.DESC);
        searchSourceBuilder.sort(scoreSortBuilder).sort(fieldSortBuilder);

        //高亮字段设置
        if (StringUtils.isNotEmpty(highlightFields)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            for(String highlightField : highlightFields.split(",")) {
                highlightBuilder.field(highlightField);
            }
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        //分页设置
        searchSourceBuilder.from((currentPage - 1) * pageSize);
        searchSourceBuilder.size(pageSize);

        //查询语句（可以在Kibana上直接执行）
        logger.info("查询语句：{}", searchSourceBuilder.toString());

        //执行查询
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);

        //查询数量
        long total = searchResponse.getHits().getTotalHits().value;
        long handled = searchResponse.getHits().getHits().length;
        logger.info("查询数量：共查询到[{}]条数据，共处理了[{}]条数据", total, handled);

        //结果集高亮处理
        if (searchResponse.status().getStatus() == 200) {
            List<Map<String, Object>> docs = highlightSearchResults(searchResponse, highlightFields);
            return new Page(currentPage, pageSize, (int) total, docs);
        }

        return new Page(currentPage, pageSize, 0, null);
    }

    /**
     * 高亮处理结果集
     *
     * @param searchResponse
     * @param highlightFields
     * @return
     */
    private List<Map<String, Object>> highlightSearchResults(SearchResponse searchResponse, String highlightFields) {
        List<Map<String, Object>> docList = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            searchHit.getSourceAsMap().put("id", searchHit.getId());

            if (StringUtils.isNotEmpty(highlightFields)) {
                for(String field : highlightFields.split(",")) {
                    if (StringUtils.isNotEmpty(field)) {
                        if(searchHit.getHighlightFields().get(field) != null) {
                            Text[] hitTexts = searchHit.getHighlightFields().get(field).getFragments();
                            if (hitTexts != null) {
                                StringBuffer sb = new StringBuffer();
                                for (Text text : hitTexts) {
                                    sb.append(text.string());
                                }
                                searchHit.getSourceAsMap().put(field, sb.toString()); //使用高亮结果集，覆盖正常结果集
                            }
                        }
                    }
                }
            }

			docList.add(searchHit.getSourceAsMap());
		}
        return docList;
    }
}
