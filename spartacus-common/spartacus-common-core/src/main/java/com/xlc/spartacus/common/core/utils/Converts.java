package com.xlc.spartacus.common.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Set;

/**
 * 转换器工具
 *
 * @author xlc, since 2021
 */
public class Converts {

	private final static void snakeToCamel(Object json) {
		if (json instanceof JSONArray) {
			JSONArray arr = (JSONArray) json;
			for (Object obj : arr) {
				snakeToCamel(obj);
			}
		} else if (json instanceof JSONObject) {
			JSONObject jo = (JSONObject) json;
			Set<String> keys = jo.keySet();
			String[] array = keys.toArray(new String[keys.size()]);
			for (String key : array) {
				Object value = jo.get(key);
				String[] key_strs = key.split("_");
				if (key_strs.length > 1) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < key_strs.length; i++) {
						String ks = key_strs[i];
						if (!"".equals(ks)) {
							if (i == 0) {
								sb.append(ks);
							} else {
								int c = ks.charAt(0);
								if (c >= 97 && c <= 122) {
									int v = c - 32;
									sb.append((char) v);
									if (ks.length() > 1) {
										sb.append(ks.substring(1));
									}
								} else {
									sb.append(ks);
								}
							}
						}
					}
					jo.remove(key);
					jo.put(sb.toString(), value);
				}
				snakeToCamel(value);
			}
		}
	}

	/**
	 * 将任何能够转换成json的对象，再将其中的下划线key转成驼峰key，最终返回json对象
	 *
	 * @param objectThatCanBeConvertToJsonString
	 * @return
	 */
	public final static Object convertSnakeToCamel(Object objectThatCanBeConvertToJsonString) {
		String json = JSON.toJSONString(objectThatCanBeConvertToJsonString);
		Object obj = JSON.parse(json);
		snakeToCamel(obj);
		return obj;
	}
}