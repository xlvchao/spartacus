package com.xlc.spartacus.resource.service;

import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.resource.exception.GlobalException;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import org.springframework.transaction.annotation.Transactional;

/**
 * 腾讯云对象存储
 *
 * 注意，上传对象的命名方式用这种，如：2019-01-30-id-xxx.png
 *
 * @author xlc, since 2021
 */
public interface CosService {


	/**
	 * 高级搜索
	 * @param searchText
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws GlobalException
	 */
	BaseResponse search(String searchText, Integer cosType, String rootPath, int currentPage, int pageSize);

	/**
	 * 重命名
	 *
	 * @param key
	 * @param newFileName
	 * @return
	 */
	BaseResponse rename(String key, String newFileName, String eventId);


	/**
	 * 批量移动cos对象
	 *
	 * @param keysStr 多个key使用英文逗号分隔
	 * @param destDirPath 目的目录的路径，比如 'image/111/222/'
	 * @return
	 */
	BaseResponse batchMove(String keysStr, String destDirPath, String eventId);

	/**
	 * 批量下载
	 *
	 * @param keysStr
	 * @return
	 */
	BaseResponse batchDownload(String keysStr);


	/**
	 * 下载文件，返回base64编码数据
	 *
	 * @param key
	 * @return
	 */
	BaseResponse download(String key);

	/**
	 * 获取标签的 词频 统计列表
	 *
	 * @return
	 */
	BaseResponse listTags();

	/**
	 * 先上传到COS，成功后再写到MYSQL，成功后再写到ES
	 *  
	 * @param parentDirPath
	 * @param fileName
	 * @param tags
	 * @param fileBytes
	 * @return
	 * @throws GlobalException
	 */
	BaseResponse webUploader(String parentDirPath, Long parentId, String fileName, String tags, byte[] fileBytes);

	/**
	 * 分页获取 cos 对象数据（注意，不包含虚拟目录对象）
	 *
	 * @param isRecursive
	 * @param dirPath 例如 'image/工作/public/'
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	BaseResponse listObjects(boolean isRecursive, String dirPath, Integer currentPage, Integer pageSize, String tag);
	
	/**
	 * 批量设置COS对象的ACL权限
	 *  
	 * @param keysStr
	 * @param aclFlag
	 * @return
	 */
	BaseResponse batchSetObjectAcl(String keysStr, Integer aclFlag, String eventId);

	/**
	 * 新建COS虚拟目录，默认设置权限为 公有读私有写
	 *
	 * @param parentDirPath
	 * @param parentId
	 * @param newDirName
	 * @return
	 */
	@Transactional
    BaseResponse createDirectory(String parentDirPath, Long parentId, String newDirName);

	/**
	 * 异步删除COS目录对象
	 *  
	 * @return
	 */
	BaseResponse deleteDirectory(String targetDirPath, String eventId);

	/**
	 * 异步批量删除 cos 对象
	 * @param keysStr 多个key，用英文逗号分隔
	 * @return
	 */
	BaseResponse batchDelete(String keysStr, String eventId);

	/**
	 * 根据COS中某个bucket下的某个根目录，生成该根目录的目录树
	 * 
	 * COS Java SDK v5没有目录操作的方法，这里自定义递归遍历某个根目录获取所有objects耗时耗力，待优化
	 *  
	 * @return
	 */
	BaseResponse getDirectoryTree(String rootDirPath);


	/**
	 * 以文件流方式上传
	 *  
	 * @param fileBytes
	 * @return 返回文件传到COS后的访问路径
	 */
	BaseResponse fileUpload(String fileName, byte[] fileBytes, String baseDirPath);


    /**
	 * 判断COS对象是否存在
	 *  
	 * @param key
	 * @return
	 * @throws CosClientException
	 * @throws CosServiceException
	 */
	boolean isExist(String key);

	/**
	 * 列出历史使用过的头像
	 *
	 * @return {@link null}
	 */
    BaseResponse loadHistoryLogos();
}
