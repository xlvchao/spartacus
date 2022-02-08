package com.xlc.spartacus.resource.mapper;

import com.xlc.spartacus.resource.pojo.CosResource;
import com.xlc.spartacus.resource.pojo.Profile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * mapper
 *
 * @author xlc, since 2021
 */
@Mapper
public interface CosResourceMapper {

    Integer save(CosResource cosResource);

    Integer saveLogo(Map<String, Object> logo);

    List<String> findAllTags();

    Integer batchMove(List<Map<String, Object>> objs);

    Integer deleteCosResourcesByIdIn(List<Long> ids);

    Integer batchSetAclFlag(Map<String, Object> param);

    Integer renameByKey(Map<String, Object> param);

    CosResource findFirstByKey(String key);

    CosResource findFirstByKeyStartsWith(String key);

    Integer countByKeyStartsWithAndCosType(Map<String, Object> param);

    List<CosResource> findByKeyStartsWithAndCosType(Map<String, Object> param);

    List<CosResource> findAllByKeyIn(List<String> keys);

    List<CosResource> findAllByCosType(Integer cosType);

    List<CosResource> findAllByKeyStartsWithAndCosType(Map<String, Object> param);

    List<CosResource> getRecursiveCosResourcesByTag(Map<String, Object> param);

    Integer getRecursiveCosResourcesCountByTag(Map<String, Object> param);

    List<CosResource> getRecursiveCosResources(Map<String, Object> param);

    Integer getRecursiveCosResourcesCount(String dirPath);

    List<CosResource> getDirectCosResourcesByTag(Map<String, Object> param);

    Integer getDirectCosResourcesCountByTag(Map<String, Object> param);

    List<CosResource> getDirectCosResources(Map<String, Object> param);

    Integer getDirectCosResourcesCount(String dirPath);

    Integer deleteAll();

    Integer saveAll(List<CosResource> resources);

    List<Profile> loadHistoryLogos();
}