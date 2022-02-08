package com.xlc.spartacus.comment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 全局处理：解决js处理long型丢失精度问题，将Long转为String
 *
 * @author xlc, since 2021
 */
@EnableWebMvc
@Configuration
public class WebJsonConverterConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //定义Json转换器
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        //定义对象映射器
        ObjectMapper objectMapper = new ObjectMapper();
        //定义对象模型
        SimpleModule simpleModule = new SimpleModule();
        //添加对长整型的转换关系
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        //将对象模型添加至对象映射器
        objectMapper.registerModule(simpleModule);
        //将对象映射器添加至Json转换器
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        //在转换器列表中添加自定义的Json转换器
        converters.add(jackson2HttpMessageConverter);
        //添加utf-8的默认String转换器
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }
}
