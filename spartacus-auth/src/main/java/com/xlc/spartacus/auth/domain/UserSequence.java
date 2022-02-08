package com.xlc.spartacus.auth.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户序列号
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSequence implements Serializable {
	private static final long serialVersionUID = 1L;

    private String userId; //providerUserId

	private Integer userSequence;
}
