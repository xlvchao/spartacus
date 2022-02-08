package com.xlc.spartacus.monitor.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 实体
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccessForbid {
	
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	private String ip;
	
	private String ipCity;
	
	private String forbidType; // 触发哪种超频访问（day/month/year）
	
	private Integer dayCount; // 访问次数
	
	private Integer monthCount; // 访问次数
	
	private Integer yearCount; // 访问次数
	
	private Integer totalCount; // 访问次数
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date operateTime;
}
