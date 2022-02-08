package com.xlc.spartacus.resource.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * 腾讯云COS资源 实体
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CosResource {
	private Long id;

	//构建目录树时用到
	private Long parentId;
	
	private String fileName;

	// COS实体对象相对根目录的路径，比如 image/工作/logo.png
	private String key;

	// 对象公网访问路径，如 https://wwww.xxx.com/image/logo.png
	@JSONField(name="url")
//	@Transient
	private String url;

	// 标签，以英文逗号','分隔
	private String tags;
	
	// 资源类型，image/png
	private String contentType;

	// cos类型，0表示实际资源对象，1表示虚拟目录
	private Integer cosType = 0;
	
	// 状态，0是可用，1是不可用（在废弃池中）
	private Integer status = 0;
	
	private String bucketName;
	
	private String region;

	private String parentDirPath; //绝对路径，比如image/111/123.jpg，那么parentDirPath: image/111/

	private String rootDirPath; //根目录，比如 image/
	
	// 初次上传时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format="yyyy-MM-dd HH:mm:ss") //fastJson序列化时的格式
	private Date lastModified;
	
	// 注意：当在客户端创建对象时，其默认公共权限是继承权限，而继承权限的返回值是1，因此在客户端创建对象时不要用继承权限，创建后一定要手动指定权限！！
	private Integer aclFlag = 2; //1是私有读写，2是公有读私有写，3是公有读写
		
}
