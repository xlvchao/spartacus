package com.xlc.spartacus.datasyner.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.xlc.spartacus.common.es.ElasticsearchService;
import com.xlc.spartacus.datasyner.constant.DatasynerConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DatasynerTest {

    @Resource
    private ElasticsearchService elasticSearchService;


    @Test
    public void searchMatch() throws Exception {

        Map<String, Object> mustMatch = new HashMap<>();
        mustMatch.put("cos_type", 0);
        mustMatch.put("root_dir_path", "cycle/");

        System.out.println(elasticSearchService.searchMatch(
                DatasynerConstant.COS_RESOURCE_INDEX_NAME,
                "怪物与美女",
                "tags,file_name",
                mustMatch,
                null,
                "last_modified",
                "tags,file_name",
                1,
                10));
    }

    @Test
    public void get() throws Exception {

        System.out.println(elasticSearchService.get(DatasynerConstant.COS_RESOURCE_INDEX_NAME, "1111111"));
    }

    @Test
    public void deleteAll() throws Exception {

        System.out.println(elasticSearchService.deleteAll(DatasynerConstant.COS_RESOURCE_INDEX_NAME));
    }


    @Test
    public void bulkDelete() throws Exception {

        List<String> ids = new ArrayList<>();
        ids.add("1111111");
        ids.add("222222");

        System.out.println(elasticSearchService.bulkDelete(DatasynerConstant.COS_RESOURCE_INDEX_NAME,ids));
    }

    @Test
    public void delete() throws Exception {

        System.out.println(elasticSearchService.delete(DatasynerConstant.COS_RESOURCE_INDEX_NAME,"1111111"));
    }

    @Test
    public void bulkUpdate() throws Exception {
        CosResource cos0 = new CosResource();
        cos0.setId(1111111L);
        cos0.setParentId(23462346L);
        cos0.setFileName("美女与野兽.jpg");
        cos0.setKey("image/meinv.jpg");
        cos0.setTags("美女与野兽");
        cos0.setContentType("image/jpg");
        cos0.setCosType(0);
        cos0.setStatus(0);
        cos0.setBucketName("tret-1256125");
        cos0.setRegion("chengdu");
        cos0.setParentDirPath("image/");
        cos0.setRootDirPath("image/");
        cos0.setLastModified(new Date());
        cos0.setAclFlag(2);


        CosResource cos3 = new CosResource();
        cos3.setId(111111111111L);
        cos3.setParentId(23462346L);
        cos3.setFileName("美女与野兽.jpg");
        cos3.setKey("image/meinv.jpg");
        cos3.setTags("美女与野兽");
        cos3.setContentType("image/jpg");
        cos3.setCosType(0);
        cos3.setStatus(0);
        cos3.setBucketName("tret-1256125");
        cos3.setRegion("chengdu");
        cos3.setParentDirPath("image/");
        cos3.setRootDirPath("image/");
        cos3.setLastModified(new Date());
        cos3.setAclFlag(2);


        CosResource cos1 = new CosResource();
        cos1.setId(222222L);
        cos1.setParentId(23462346L);
        cos1.setFileName("傻逼.jpg");
        cos1.setKey("image/yeshou.jpg");
        cos1.setTags("怪物，傻逼,美女");
        cos1.setContentType("image/jpg");
        cos1.setCosType(0);
        cos1.setStatus(0);
        cos1.setBucketName("tret-1256125");
        cos1.setRegion("chengdu");
        cos1.setParentDirPath("image/");
        cos1.setRootDirPath("image/");
        cos1.setLastModified(new Date());
        cos1.setAclFlag(2);

        Map<String, Map<String, Object>> docs = new HashMap<>();
        docs.put(cos0.getId()+"", toUnderlineJSONString(cos0));
        docs.put(cos1.getId()+"", toUnderlineJSONString(cos1));
        docs.put(cos3.getId()+"", toUnderlineJSONString(cos3));

        elasticSearchService.bulkUpdate(DatasynerConstant.COS_RESOURCE_INDEX_NAME, docs);
    }

    @Test
    public void update() throws Exception {
        CosResource cos0 = new CosResource();
        cos0.setId(1111111L);
        cos0.setParentId(23462346L);
        cos0.setFileName("meinv.jpg");
        cos0.setKey("image/meinv.jpg");
        cos0.setTags("美女");
        cos0.setContentType("image/jpg");
        cos0.setCosType(0);
        cos0.setStatus(0);
        cos0.setBucketName("tret-1256125555");
        cos0.setRegion("chengduuuu");
        cos0.setParentDirPath("image/");
        cos0.setRootDirPath("image/");
        cos0.setLastModified(new Date());
        cos0.setAclFlag(2);

        Map<String, Object> doc = toUnderlineJSONString(cos0);

        elasticSearchService.update(DatasynerConstant.COS_RESOURCE_INDEX_NAME, cos0.getId()+"", doc);
    }

    @Test
    public void bulkAdd() throws Exception {
        CosResource cos0 = new CosResource();
        cos0.setId(1111111L);
        cos0.setParentId(23462346L);
        cos0.setFileName("美女.jpg");
        cos0.setKey("cycle/美女.jpg");
        cos0.setTags("美女");
        cos0.setContentType("image/jpg");
        cos0.setCosType(0);
        cos0.setStatus(0);
        cos0.setBucketName("tret-1256125");
        cos0.setRegion("chengdu");
        cos0.setParentDirPath("cycle/");
        cos0.setRootDirPath("cycle/");
        cos0.setLastModified(new Date(2019,11,11));
        cos0.setAclFlag(2);


        CosResource cos1 = new CosResource();
        cos1.setId(222222L);
        cos1.setParentId(23462346L);
        cos1.setFileName("怪兽.jpg");
        cos1.setKey("image/怪兽.jpg");
        cos1.setTags("怪兽");
        cos1.setContentType("image/jpg");
        cos1.setCosType(1);
        cos1.setStatus(0);
        cos1.setBucketName("tret-1256125");
        cos1.setRegion("chengdu");
        cos1.setParentDirPath("image/");
        cos1.setRootDirPath("image/");
        cos1.setLastModified(new Date());
        cos1.setAclFlag(2);

        Map<String, Map<String, Object>> docs = new HashMap<>();
        docs.put(cos0.getId()+"", toUnderlineJSONString(cos0));
        docs.put(cos1.getId()+"", toUnderlineJSONString(cos1));

        elasticSearchService.bulkAdd(DatasynerConstant.COS_RESOURCE_INDEX_NAME, docs);
    }

    @Test
    public void add() throws Exception {
        CosResource cos0 = new CosResource();
        cos0.setId(888888L);
        cos0.setParentId(23462346L);
        cos0.setFileName("meinv.jpg");
        cos0.setKey("image/meinv.jpg");
        cos0.setTags("美女啊");
        cos0.setContentType("image/jpg");
        cos0.setCosType(0);
        cos0.setStatus(0);
        cos0.setBucketName("tret-1256125");
        cos0.setRegion("chengdu");
        cos0.setParentDirPath("image/");
        cos0.setRootDirPath("image/");
        cos0.setLastModified(new Date());
        cos0.setAclFlag(2);

        elasticSearchService.add(DatasynerConstant.COS_RESOURCE_INDEX_NAME,cos0.getId()+"", toUnderlineJSONString(cos0));
    }


    /**
     * 驼峰转下划线
     * @param javaBean
     * @return
     * @throws JsonProcessingException
     */
    public static Map toUnderlineJSONString(Object javaBean) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        String reqJson = mapper.writeValueAsString(javaBean);

//        ParserConfig.getGlobalInstance().propertyNamingStrategy = com.alibaba.fastjson.PropertyNamingStrategy.SnakeCase;

        // 序列化配置
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = com.alibaba.fastjson.PropertyNamingStrategy.SnakeCase;

        String reqJson = JSON.toJSONString(javaBean, config);

        return JSON.parseObject(reqJson, Map.class);
    }


}
