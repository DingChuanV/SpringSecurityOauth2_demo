package com.uin.config;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglufei
 * @description: 配置token内容增强器
 * @date 2022/4/12/4:21 PM
 */
public class JwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> info = new HashMap<>();
        //我们需要增强的内容
        info.put("enhance", "enhance info");
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);
        return oAuth2AccessToken;
    }
}
