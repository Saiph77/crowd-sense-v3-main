package com.fzu.crowdsense.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 阿里云SMS短信服务
 * <p>
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-04-24
 **/

@Configuration
public class SmsConfig {

    private final String ACCESS_KEY_ID = "LTAI5tGaNgM9ywWXshLAsyES";

    private final String ACCESS_KEY_SECRET = "Rf2KU8hANXJsKoDzlsejxoUpBZu5do";

    private final String REGION_ID = "cn-hangzhou";


    @Bean
    public AsyncClient getSmsClient() {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(ACCESS_KEY_ID)
                .accessKeySecret(ACCESS_KEY_SECRET)
                .build());

        return AsyncClient.builder()
                .region(REGION_ID)
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                )
                .build();
    }
}
