package com.xlc.spartacus.resource.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统用户 实体
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Profile implements Serializable {
	private static final long serialVersionUID = 1L;

    private Long id;

	private String headImg;

	private String nickname; //username

	private String title;

	private String age;

	private String github;

	private String experience;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //格式化前端传过来的时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8") //返回给前端的时间格式
	private Date addTime;

}
