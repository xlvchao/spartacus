package com.xlc.spartacus.system.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 友情链接 实体
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FriendLink {
    private Long id;

    private String siteName; //网站名称

    private String siteAddress; //网站地址

    private Integer isValid = 0; //是否有效，1有效，0无效

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //格式化前端传过来的时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8") //返回给前端的时间格式
    private Date addTime;
}
