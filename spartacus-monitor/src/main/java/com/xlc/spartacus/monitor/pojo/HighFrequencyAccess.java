package com.xlc.spartacus.monitor.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 实体
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HighFrequencyAccess {
	
	private String ip;
	
	private String ipCity;
	
	private Object count; //访问次数
	
	private Map<String, Object> analysis; //分时间段统计
	
	private Integer forbidden = 0; //是否被封禁，0不是，1是，默认不是
	
}
