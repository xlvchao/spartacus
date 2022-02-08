package com.xlc.spartacus.auth.common;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 腾讯云短信发送工具
 *
 * @author xlc, since 2021
 */
@Component
public class SmsUtils {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spartacus.tsms.appid}")
    private Integer appid;

    @Value("${spartacus.tsms.appkey}")
    private String appkey;

    @Value("${spartacus.tsms.templateId}")
    private Integer templateId; // NOTE: 这里的模板 ID`7839`只是示例，真实的模板 ID 需要在短信控制台中申请

    @Value("${spartacus.tsms.smsSign}")
    private String smsSign; // NOTE: 签名参数使用的是`签名内容`，而不是`签名ID`。这里的签名"腾讯云"只是示例，真实的签名需要在短信控制台申请


    public void sendSmsCode(String mobile, String code) {
        try {
            logger.info("开始调用腾讯云接口向手机"+mobile+"发送验证码：" + code);
            String[] params = {code};
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.sendWithParam("86", mobile, templateId, params, smsSign, "", "");
            logger.info("调用腾讯云接口向手机"+mobile+"发送验证码的处理结果：" + result);
        } catch (HTTPException e) {
            // HTTP 响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // JSON 解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络 IO 错误
            e.printStackTrace();
        }
    }
}
