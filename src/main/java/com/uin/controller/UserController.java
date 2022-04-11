package com.uin.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wanglufei
 * @description: 类似于client 客户端
 * @date 2022/4/11/8:05 PM
 */
@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * 获取当前user
     *
     * @param authentication
     * @return java.lang.String
     * @author wanglufei
     * @date 2022/4/11 8:09 PM
     */
    @RequestMapping("/getCurrentUser")
    //authentication 认证
    public Object getCurrentUser(Authentication authentication) {
        return authentication.getPrincipal();
    }

}
