package com.xlc.spartacus.system.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 登陆记录 实体
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LoginRecord {
    private Long id;

    private String username;

    private String userType;

    private String ip;

    private String province;

    private String city;

    private String client;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //格式化前端传过来的时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8") //返回给前端的时间格式
    private Date loginTime;

}
