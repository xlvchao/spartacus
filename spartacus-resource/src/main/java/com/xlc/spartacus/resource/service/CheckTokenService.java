package com.xlc.spartacus.resource.service;

import com.xlc.spartacus.common.core.common.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 服务之间通过openfeign调用
 *
 * @author xlc, since 2021
 */
@Component
@FeignClient(value = "SPARTACUS-GATEWAY")
public interface CheckTokenService {

    @RequestMapping(value = "/jwt/decode/{token}", method = RequestMethod.GET)
    CommonResponse checkToken(@PathVariable String token);

}