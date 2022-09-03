package com.xlc.spartacus.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置加载
 *
 * @author xlc, since 2021
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    private String password;
    private Cluster cluster;


    public static class Cluster {
        private List<String> nodes;

        public List<String> getNodes() {
            return nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = new ArrayList<>();
            nodes.forEach(i -> this.nodes.add("redis://" + i));
        }

        @Override
        public String toString() {
            return "Cluster{" +
                    "nodes=" + nodes +
                    '}';
        }
    }

}
