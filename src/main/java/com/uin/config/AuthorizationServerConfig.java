package com.uin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * @author wanglufei
 * @description: 授权服务器的配置
 * 用来对资源拥有者的身份进行认证、对访问资源进行授权。客户端要想访问资源需要通过认证服务器由资源拥有者授权后可访问。
 * @date 2022/4/11/7:38 PM
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 授权服务器的4个端点
     * * - `Authorize Endpoint` ：授权端点，进行授权
     * * - `Token Endpoint` ：令牌端点，进过授权拿到对应的Token
     * * - `Introspection Endpoint`：校验端点，校验Token的合法性
     * * - `Revocat ion Endpoint` ：撤销端点，撒销授权
     *
     * @param clients
     * @author wanglufei
     * @date 2022/4/11 7:47 PM
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                //配置client Id
                .withClient("admin")
                //client-secret
                .secret(passwordEncoder.encode("112233"))
                //配置访问token的有效期
                .accessTokenValiditySeconds(3600)
                //配置重定向的跳转，用于授权成功之后的跳转
                .redirectUris("http://www.baidu.com")
                //作用域
                .scopes("all")
                //Grant_type  授权码模式
                .authorizedGrantTypes("authorization_code");
    }

}
