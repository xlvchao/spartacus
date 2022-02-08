package com.xlc.spartacus.article.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 实体
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Category {

	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	private String cname;
}
