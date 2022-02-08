package com.xlc.spartacus.common.es;

import com.xlc.spartacus.common.core.pojo.Page;

import java.util.List;
import java.util.Map;

/**
 * es服务定义
 *
 * @author xlc, since 2021
 */
public interface ElasticsearchService {

    /**
     * 插入文档
     *
     * @param index
     * @param id
     * @param doc
     * @return
     * @throws Exception
     */
    boolean add(String index, String id, Map<String, Object> doc) throws Exception;

    /**
     * 批量插入文档
     *
     * @param index
     * @param docs
     * @return
     * @throws Exception
     */
    boolean bulkAdd(String index, Map<String, Map<String, Object>> docs) throws Exception;

    /**
     * 根据id获取文档
     *
     * @param index
     * @param id
     * @return
     * @throws Exception
     */
    Map<String, Object> get(String index, String id) throws Exception;

    /**
     * 更新文档
     *
     * @param index
     * @param id
     * @param doc
     * @return
     * @throws Exception
     */
    boolean update(String index, String id, Map<String, Object> doc) throws Exception;

    /**
     * 批量更新文档
     *
     * @param index
     * @param docs
     * @return
     * @throws Exception
     */
    boolean bulkUpdate(String index, Map<String, Map<String, Object>> docs) throws Exception;

    /**
     * 删除文档
     *
     * @param index
     * @param id
     * @return
     * @throws Exception
     */
    boolean delete(String index, String id) throws Exception;

    /**
     * 批量删除文档
     *
     * @param index
     * @param ids
     * @return
     * @throws Exception
     */
    boolean bulkDelete(String index, List<String> ids) throws Exception;

    /**
     * 删除所有文档
     *
     * @param index
     * @return
     * @throws Exception
     */
    boolean deleteAll(String index) throws Exception;

    /**
     * 智能搜索
     *
     * @param index 索引名称
     * @param searchText 搜索内容
     * @param matchFields 搜索内容需要匹配哪些字段
     * @param mustMatchs 必须完全匹配的键值对
     * @param rangeMatchs 范围查询需要匹配的键值对（value[0]是范围起始值，value[1]是范围结束值）
     * @param sortField 排序字段
     * @param highlightFields 高亮字段（用英文逗号分隔）
     * @param currentPage 当前页（从0开始）
     * @param pageSize 页大小
     * @return
     * @throws Exception
     */
    Page searchMatch(String index, String searchText, String matchFields, Map<String, Object> mustMatchs, Map<String, Map<String, Object>> rangeMatchs, String sortField, String highlightFields, int currentPage, int pageSize) throws Exception;

}
