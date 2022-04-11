package com.uin.service;

import com.uin.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author wanglufei
 * @description: 自定义登陆逻辑
 * @date 2022/4/11/6:29 PM
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 自定义登陆方法
     *
     * @param username
     * @return org.springframework.security.core.userdetails.UserDetails
     * @author wanglufei
     * @date 2022/4/11 6:31 PM
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = passwordEncoder.encode("123456");
        System.out.println(password);
        return new User("admin", password,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
