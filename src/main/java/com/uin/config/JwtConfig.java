package com.uin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author wanglufei
 * @description: jwtToken配置类
 * @date 2022/4/12/3:48 PM
 */
@Configuration
public class JwtConfig {

    /**
     * 将授权码存放在jwtToken
     *
     * @return org.springframework.security.oauth2.provider.token.TokenStore
     * @author wanglufei
     * @date 2022/4/12 3:55 PM
     */
    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * jwtToken和AccessToken的相互转换
     *
     * @return org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
     * @author wanglufei
     * @date 2022/4/12 3:54 PM
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //添加认证JwtToken的密钥
        jwtAccessTokenConverter.setSigningKey("test_key");
        return jwtAccessTokenConverter;
    }
}
