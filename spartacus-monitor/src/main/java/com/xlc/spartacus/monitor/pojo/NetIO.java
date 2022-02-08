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
public class NetIO {
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;

	private String ip;

	private float ioIn; //入网流量
	
	private float ioOut; //出网流量

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date insertDate;
}
