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
public class Access {
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	private String url;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date accessTime;

	private String clientCurrentId; //客户端浏览器当日唯一身份ID
	
	private String ip;
	
	private String ipCity;
	
	private String clientId;

//	@Transient
	private Integer forbidden = 0; //是否被封禁，0不是，1是，默认不是
		
}
