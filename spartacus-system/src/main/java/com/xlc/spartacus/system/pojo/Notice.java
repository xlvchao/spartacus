package com.xlc.spartacus.system.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 通知公告 实体
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Notice {
    private Long id;

    private String content;

    private Integer isForSite = 0;

    private Integer isForChat = 0;

    private Integer isEnabled = 0; //是否启用

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //格式化前端传过来的时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8") //返回给前端的时间格式
    private Date createTime;

}
