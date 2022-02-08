package com.xlc.spartacus.auth.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xlc.spartacus.auth.common.CommonResponse;
import com.xlc.spartacus.auth.config.CommonProperties;
import com.xlc.spartacus.auth.core.properties.SecurityConstants;
import com.xlc.spartacus.auth.task.AsyncTask;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.xlc.spartacus.common.core.utils.HttpUtils;
import com.xlc.spartacus.common.core.utils.IPUtils;
import com.xlc.spartacus.common.core.utils.Snowflake;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 成功处理器，登录成功给页面返回提示信息
 *
 * @author xlc, since 2021
 */
@Component("myAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CommonProperties commonProperties;

	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private AuthorizationServerTokenServices defaultAuthorizationServerTokenServices;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RedisTemplate redisTemplate;

	@Resource
	private AsyncTask task;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		logger.info("登录成功");
		logger.info("登陆URL：" + request.getRequestURI());

		//异步保存登录信息
		Map<String, Object> loginInfo = getIpAndAreaInfo(request);

		// 非APP环境，暂时不需要前端传递
		/*String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Basic ")) {
			throw new UnapprovedClientAuthenticationException("请求头中无client信息");
		}
		String[] tokens = this.extractAndDecodeHeader(header, request);

		assert tokens.length == 2;

		String clientId = tokens[0];
		String clientSecret = tokens[1];*/

		String clientId = "id";
		String clientSecret = "secret";

		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
		if(clientDetails == null) {
			throw new UnapprovedClientAuthenticationException("clientId:"+clientId+"对应的配置信息不存在！");
		} else if(!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
			throw new UnapprovedClientAuthenticationException("clientSecret:"+clientSecret+"与配置不匹配！");
		}

		TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(), "custom");
		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

		OAuth2AccessToken token = defaultAuthorizationServerTokenServices.createAccessToken(oAuth2Authentication);
		System.out.println("生成的token:" + token.getValue());

		//缓存token，用于定时心跳检测
		//BCrypt：一种加盐的单向Hash，不可逆的加密算法，同一种明文（plaintext），每次加密后的密文都不一样，而且不可反向破解生成明文，破解难度很大。
		//MD5：是不加盐的单向Hash，不可逆的加密算法，同一个密码经过hash的时候生成的是同一个hash值，在少数情况下，有些经过md5加密的密文可能将会被破解。
		//String keySuffix = DigestUtils.md5DigestAsHex(token.getValue().getBytes());
		//redisTemplate.opsForValue().set("CURRENT_ONLINE_USER:" + token.getValue(), CommonUtils.getDateString(), 5, TimeUnit.MINUTES);
		redisTemplate.opsForValue().set("CURRENT_ONLINE_USER:" + token.getValue(), token.getAdditionalInformation().get("userSequence"), 5, TimeUnit.MINUTES);

		JSONObject tokenJson = JSON.parseObject(objectMapper.writeValueAsString(token));

		response.setStatus(HttpStatus.OK.value());
		if(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM.equals(request.getRequestURI()) //表单登陆
			|| SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE.equals(request.getRequestURI())) { //手机登陆
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(CommonResponse.success(tokenJson)));

			loginInfo.put("client", "spartacus-friday");
			loginInfo.put("username", tokenJson.get("providerUserId"));

		} else { //社交登陆
			response.setContentType("text/html;charset=UTF-8");
			String commonResult = objectMapper.writeValueAsString(CommonResponse.success(tokenJson));
			String respContent = "<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\"content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"><meta http-equiv=\"X-UA-Compatible\"content=\"ie=edge\"><title>oauth spartacus</title></head><body>登陆中...<script>window.onload=function(){window.opener.postMessage("+commonResult+",\"*\");window.close()}</script></body></html>";
			response.getWriter().write(respContent);

			loginInfo.put("client", "spartacus-sunday");
			loginInfo.put("username", tokenJson.get("nickname"));
		}

		//异步保存登录信息
		loginInfo.put("id", Snowflake.generateId());
		loginInfo.put("userType", tokenJson.get("providerId"));
		loginInfo.put("loginTime", new Date());
		task.asyncRecordLoginInfo(loginInfo);
	}

	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws IOException {
		byte[] base64Token = header.substring(6).getBytes("UTF-8");

		byte[] decoded;
		try {
			decoded = Base64.decode(base64Token);
		} catch (IllegalArgumentException var7) {
			throw new BadCredentialsException("Failed to decode basic authentication token");
		}

		String token = new String(decoded, "UTF-8");
		int delim = token.indexOf(":");
		if (delim == -1) {
			throw new BadCredentialsException("Invalid basic authentication token");
		} else {
			return new String[]{token.substring(0, delim), token.substring(delim + 1)};
		}
	}

	private Map<String, Object> getIpAndAreaInfo(HttpServletRequest request) {
		Map<String, Object> info = new HashMap<>();

		try {
			String ip = IPUtils.getIp(request);
			info.put("ip", ip);
			String url = commonProperties.getBaiduMapUrl() + "&ak=" + commonProperties.getBaiduMapAk() + "&ip=" + ip;
			JSONObject jsonObject = JSONObject.parseObject(HttpUtils.get(url));
			System.out.println(jsonObject);
			if (jsonObject.getInteger("status") == 0) {
				String province = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("province");
				String city = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("city");
				info.put("province", province);
				info.put("city", city);
			}
		} catch (Exception e) {
			logger.error("获取位置信息发生异常！", e);
		}

		return info;
	}

}
