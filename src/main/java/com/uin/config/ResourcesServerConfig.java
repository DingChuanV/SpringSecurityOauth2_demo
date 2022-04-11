package com.uin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @author wanglufei
 * @description: 资源服务器的配置
 * 通常为用户，也可以是应用程序，既该资源的拥有者。
 * @date 2022/4/11/7:38 PM
 */
@Configuration
@EnableResourceServer
public class ResourcesServerConfig extends ResourceServerConfigurerAdapter {
    /**
     * @param http
     * @author wanglufei
     * @date 2022/4/11 8:00 PM
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //所有的访问都需要认证访问
        http.authorizeRequests().anyRequest().authenticated();
        //唯独user 可以访问 放行我们的资源
        http.requestMatchers().antMatchers("/user/**");
    }
}
