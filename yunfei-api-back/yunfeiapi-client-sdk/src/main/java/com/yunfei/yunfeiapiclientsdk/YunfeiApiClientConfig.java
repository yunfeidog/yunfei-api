package com.yunfei.yunfeiapiclientsdk;

import com.yunfei.yunfeiapiclientsdk.client.YunfeiApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 客户端配置
 */
@Configuration
@ConfigurationProperties("yunfeiapi.client")
@Data
@ComponentScan
public class YunfeiApiClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public YunfeiApiClient yunfeiapiClient() {
        return new YunfeiApiClient(accessKey, secretKey);
    }
}
