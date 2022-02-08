package com.xlc.spartacus.gateway.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * apollo配置读取工具
 *
 * @author xlc, since 2021
 */
@Component("apolloConfigHelper")
public class ApolloConfigHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String errorMsg = "配置中心连接异常！";


    public Map<String, String> getPropertyMap(String namespace) {
        try {
            Map<String, String> propertyMap = new HashMap<>();
            Config config = ConfigService.getConfig(namespace);
            Set<String> propertyNames = config.getPropertyNames();
            for(String key : propertyNames) {
                String value = config.getProperty(key, "");
                propertyMap.put(key, value);
            }
            return propertyMap;
        } catch (Exception e) {
            logger.error(errorMsg, namespace, e);
            return null;
        }
    }

    public String getStringProperty(String namespace, String key, String defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            String value = config.getProperty(key, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public String getStringProperty(String namespace, String key) {
        String defaultValue = "";
        try {
            Config config = ConfigService.getConfig(namespace);
            String value = config.getProperty(key, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public Boolean getBooleanProperty(String namespace, String key, boolean defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            Boolean value = config.getBooleanProperty(key, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public Byte getByteProperty(String namespace, String key, Byte defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            Byte value = config.getByteProperty(key, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public Double getDoubleProperty(String namespace, String key, Double defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            Double value = config.getDoubleProperty(key, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public Float getFloatProperty(String namespace, String key, Float defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            Float value = config.getFloatProperty(key, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public Integer getIntProperty(String namespace, String key, Integer defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            Integer value = config.getIntProperty(key, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public Long getLongProperty(String namespace, String key, Long defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            Long value = config.getLongProperty(key, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public Short getShortProperty(String namespace, String key, Short defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            Short value = config.getShortProperty(key, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public String[] getArrayProperty(String namespace, String key, final String delimiter, String[] defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            String[] value = config.getArrayProperty(key, delimiter, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public Date getDateProperty(String namespace, String key, Date defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            Date dateProperty = config.getDateProperty(key, defaultValue);
            return dateProperty != null ? dateProperty : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }

    public Date getDateProperty(String namespace, String key, String format, Date defaultValue) {
        try {
            Config config = ConfigService.getConfig(namespace);
            Date value = config.getDateProperty(key, format, defaultValue);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            logger.error(errorMsg, key, e);
            return defaultValue;
        }
    }
}