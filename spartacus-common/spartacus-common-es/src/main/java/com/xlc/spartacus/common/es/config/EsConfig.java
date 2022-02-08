package com.xlc.spartacus.common.es.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * es配置
 *
 * @author xlc, since 2021
 */
@Configuration
@ConfigurationProperties(prefix = "elasticsearch.server")
public class EsConfig {
    private String addresses;
    private String username;
    private String password;

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() {

        List<HttpHost> hostList = new ArrayList<>();
        for(String addr : addresses.split(",")) {
            String[] device = addr.split(":");
            hostList.add(new HttpHost(device[0], Integer.parseInt(device[1]), "http"));
        }

        HttpHost[] hosts = new HttpHost[hostList.size()];
        hostList.toArray(hosts);

        RestClientBuilder builder = RestClient.builder(hosts);

        // 用户名、密码
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
