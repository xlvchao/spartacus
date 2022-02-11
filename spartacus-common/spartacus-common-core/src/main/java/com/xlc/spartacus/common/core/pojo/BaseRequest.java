package com.xlc.spartacus.common.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 通用请求实体
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest {
	private Integer currentPage = 0;
	private Integer pageSize = 6;
	
	private Integer status = 0; // 文章/评论：0（已发布/待审核）、1（已撤回/审核通过）、2（草稿箱/已删除）
	
	private String searchText; //搜索内容
	
	private Long articleId; //评论id

	//新增系统用户

	// 更新账户信息
	String providerUserId;
	String imageUrl;
	String displayName;
	String oldSecret;
	String newSecret;

	// 查询聊天记录
	String belongProviderUserId;
	String fromProviderUserId;
	String toProviderUserId;

	//////
	private Long id;
	private String title;
	private String author;
	private String labels;
//	private String pictures;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date publishTime;

	//	private Integer commentNumber = 0;
//	private Integer scanNumber = 0;
	private String cname;
	private String fromWhere; // 原创，摘抄，转载
	private Integer isTop; // 是否置顶，0不是，1是
	private String brief;
	private String content;

	//////
	
	//用于获取天气信息
	private String cityname;
	private String cityid;//城市代号
	private String ip;
	private String lon;//经度，如：116.322987
	private String lat;//纬度，如：39.983424
	
	//用于查询浏览记录（today/month/year/all）
	private String flag;
	
	//用于封禁/解封ip
	private boolean forbidden;
	// private String ip; //上面已经有了
	private String ipCity;
	
	// private String ip; //上面已经有了
	private Integer port;
	private String device; //cpu、memory、swap、disk、net
	
	//生成COS目录树的根目录
	private String rootDirPath;

	private String dirPath;
	private Boolean isRecursive;
	
	//新建COS目录
	private String parentDirPath;
	private Long parentId;
	private String newDirName;
	
	//删除COS目录
	private String targetDirPath;
	private String subAddress;
	
	//设置COS对象的ACL
	private String key;
	private Integer aclFlag; //1是私有读写，2是公有读私有写，3是公有读写

	//按标签搜索
	private String tag;

	private Integer cosType;
	private String rootPath;

	//多个key，用英文逗号分隔
	private String keysStr;

	private String destDirPath;

	private String newFileName;

	//查询登陆记录
	private String username;
	private String userType;
	private String city;
	private String client;
	private String loginTimeStart;
	private String loginTimeEnd;

	//通知公告
	//private String content;
	private Integer isForSite;
	private Integer isForChat;
	private Integer isEnabled;

	//友情链接
	private String siteName;
	private String siteAddress;
	private Integer isValid;

	//系统账号
	private String userId;
}
