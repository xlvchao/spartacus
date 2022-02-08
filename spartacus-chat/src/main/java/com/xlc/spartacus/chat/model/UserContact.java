package com.xlc.spartacus.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户关系实体
 *
 * @author xlc, since 2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserContact {

	private Long id;
	private String providerUserId; //用户PID
	private String contactProviderUserId; //联系人PID

}
