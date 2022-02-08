package com.xlc.spartacus.auth.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 参考GenericFastJsonRedisSerializer
 *
 * @author xlc, since 2021
 */
public class FastJsonRedisSerializer implements RedisSerializer<Object> {
    private static final ParserConfig defaultRedisConfig = new ParserConfig();

    static {
        defaultRedisConfig.setAutoTypeSupport(true);

        /**
         * 2017年3月15日，fastjson官方发布安全升级公告，该公告介绍fastjson在1.2.24及之前的版本存在代码执行漏洞，
         * 当恶意攻击者提交一个精心构造的序列化数据到服务端时，由于fastjson在反序列化时存在漏洞，可导致远程任意代码执行，
         * 自1.2.25及之后的版本，禁用了部分autotype的功能，也就是”@type”这种指定类型的功能会被限制在一定范围内使用，
         * 而由于反序列化对象时，需要检查是否开启了autotype，所以如果反序列化检查时，autotype没有开启，就会报错。
         */
        defaultRedisConfig.getGlobalInstance()
                .addAccept("com.xlc.spartacus.auth.core.code.ValidateCode");
    }

    public byte[] serialize(Object object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        } else {
            try {
                return JSON.toJSONBytes(object, new SerializerFeature[]{SerializerFeature.WriteClassName});
            } catch (Exception var3) {
                throw new SerializationException("Could not serialize: " + var3.getMessage(), var3);
            }
        }
    }

    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes != null && bytes.length != 0) {
            try {
                return JSON.parseObject(new String(bytes, IOUtils.UTF8), Object.class, defaultRedisConfig, new Feature[0]);
            } catch (Exception var3) {
                throw new SerializationException("Could not deserialize: " + var3.getMessage(), var3);
            }
        } else {
            return null;
        }
    }

}