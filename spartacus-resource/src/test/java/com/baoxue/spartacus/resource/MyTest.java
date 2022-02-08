package com.xlc.spartacus.resource;

import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.xlc.spartacus.resource.pojo.CosResource;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class MyTest {

    @Test
    public void concat() {
        System.out.println(CommonUtils.concat("wwww", "ttt", "333"));
        System.out.println(CommonUtils.concat(""));
        System.out.println(CommonUtils.concat("wwww", 20.444444, "333"));


        /*CosResource cosResource = new CosResource();
        String key = "image/123/";
        Long id = Snowflake.generateId();
        cosResource.setId(id);
        cosResource.setParentId(parentId);
        cosResource.setKey(key);
        cosResource.setFileName(newDirName);
        cosResource.setRegion(blogProperties.getBucketRegion());
        cosResource.setBucketName(blogProperties.getBucketName());
        cosResource.setStatus(0);
        cosResource.setCosType(1); //目录
        cosResource.setAclFlag(2);
        cosResource.setLastModified(new Date());*/
    }

    public static void main(String[] args) {
        // 1 初始化用户身份信息（secretId, secretKey）。
        String secretId = "AKID86sRmtlMincBUXHsACyTihQvAAvP3aIk";
        String secretKey = "KyPN5X5RYP1NULwOcbMsAE3dx1qPilZb";
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region("ap-chengdu");
        ClientConfig clientConfig = new ClientConfig(region);
        COSClient cosClient = new COSClient(cred, clientConfig);
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        String bucketName = "tret-1251733385";
        listObjectsRequest.setBucketName(bucketName);

//        System.out.println(cosClient.getObjectAcl(bucketName, "image/1111/45447659675754496-888888.JPG"));

        /*AccessControlList accessCtrlList = cosClient.getObjectAcl(bucketName, "image/1111/sfsf2342/");
        if (accessCtrlList.isExistDefaultAcl()) {
            System.out.println(accessCtrlList.getCannedAccessControl());
        }

        System.out.println(cosClient.getBucketAcl(bucketName));

        cosClient.shutdown();*/



        listObjectsRequest.setPrefix("image/");
        listObjectsRequest.setDelimiter("/");
        listObjectsRequest.setMaxKeys(1000);
        ObjectListing objectListing = null;
        do {
            try {
                objectListing = cosClient.listObjects(listObjectsRequest);
            } catch (CosServiceException e) {
                e.printStackTrace();
                return;
            } catch (CosClientException e) {
                e.printStackTrace();
                return;
            }
            // common prefix表示表示被delimiter截断的路径, 如delimter设置为/, common prefix则表示所有子目录的路径
            List<String> commonPrefixs = objectListing.getCommonPrefixes();
            for(String prefix : commonPrefixs) {
                System.out.println(prefix);
            }

            // object summary表示所有列出的object列表
            /*List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                // 文件的路径key
                String key = cosObjectSummary.getKey();
                // 文件的etag
                String etag = cosObjectSummary.getETag();
                // 文件的长度
                long fileSize = cosObjectSummary.getSize();
                // 文件的存储类型
                String storageClasses = cosObjectSummary.getStorageClass();
            }*/
            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());

        String s = "12/345/";
        System.out.println(s.lastIndexOf("/", s.length()-2));
    }

}
