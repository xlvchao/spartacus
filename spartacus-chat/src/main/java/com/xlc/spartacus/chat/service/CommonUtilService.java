package com.xlc.spartacus.chat.service;

import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.common.core.common.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 通用方法封装
 *
 * @author xlc, since 2021
 */
@Component
public class CommonUtilService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CheckTokenService checkTokenService;

    /**
     * 检查token是否有效
     *
     * @param token
     * @return
     */
    public CommonResponse checkTokenValid(String token) {
        try {
            if(StringUtils.isBlank(token)) {
                return CommonResponse.validateFailed();
            }

            logger.info("开始校验token: "+ token);
            CommonResponse checkResult = checkTokenService.checkToken(token);
            logger.info("校验token完成: "+ checkResult);

            if(Response.SUCCESS.getCode().equals(checkResult.getCode())) {
                Map<String, Object> data = (Map<String, Object>) checkResult.getData();
                Map<String, Object> claims = (Map<String, Object>) data.get("claims");
                List<String> authorities = (List<String>) claims.get("authorities");
                if(authorities.contains("ROLE_ADMIN") || authorities.contains("ROLE_USER")) {
                    claims.remove("user_name");
                    claims.remove("scope");
                    claims.remove("exp");
                    claims.remove("iat");
                    claims.remove("jti");
                    claims.remove("client_id");
                    checkResult.setData(claims);
                    return checkResult;
                }
            }
        } catch (Exception e) {
            logger.error("校验token发生异常！", e);
        }
        return CommonResponse.validateFailed();
    }
}
