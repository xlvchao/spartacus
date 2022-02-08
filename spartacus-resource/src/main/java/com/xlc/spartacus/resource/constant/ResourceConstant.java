package com.xlc.spartacus.resource.constant;

import com.qcloud.cos.model.CannedAccessControlList;

import java.util.HashMap;

/**
 * 常量
 *
 * @author xlc, since 2021
 */
public class ResourceConstant {

	public static final String COS_RESOURCE_TAGS = "cos:resource:tags";
	
	public static final String COS_RESOURCE_INDEX_NAME = "cos_resource_index";
	public static final String COS_RESOURCE_TYPE_NAME = "cos_resource_type";

	public static final HashMap<Integer, CannedAccessControlList> ACL_MAP;
	static {
		ACL_MAP = new HashMap<Integer, CannedAccessControlList>();
		ACL_MAP.put(1, CannedAccessControlList.Private);
		ACL_MAP.put(2, CannedAccessControlList.PublicRead);
		ACL_MAP.put(3, CannedAccessControlList.PublicReadWrite);
	}

	public static final String FILE_READ_STATUS_CHANGE_NOTICE_DESCRIBE_ADDRESS = "/fileReadStatusChangeNotice";
}
