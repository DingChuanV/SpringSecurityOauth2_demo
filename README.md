# SpringSecurityOauth2_demo
SpringBoot整合SpingSecurityOAuth2，并实现授权码模式。

新增内容SpringSecurityOAuth2整合Jwt。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112024625.png" alt="image-20220411175444108" style="zoom:50%;" />

实现流程

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112342648.png" alt="image-20220411173158704" style="zoom:50%;" />

## 🩴实验步骤

1. 导入依赖
```xml
    <properties>
        <java.version>1.8</java.version>
        <!--SpringCloud版本-->
        <spring-cloud.version>Greenwich.SR2</spring-cloud.version>
    </properties>
    <dependencies>
        <!--oauth2依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-oauth2</artifactId>
            <version>2.2.5.RELEASE</version>
        </dependency>
        <!--SpringSecurity依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-security</artifactId>
            <version>2.2.5.RELEASE</version>
        </dependency>
        <!--web组件-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--test组件-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
    </dependencies>

    <!--SpringCloud依赖-->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

2. 自定义登陆逻辑并配置WebSecurity相关的配置


```java
/**
 * @author wanglufei
 * @description: 自定义登陆逻辑
 * @date 2022/4/11/6:29 PM
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;

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
        return new User("admin", password,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
```

```java
/**
 * @author wanglufei
 * @description: SpringSecurity配置类
 * @date 2022/4/11/6:31 PM
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 自定义加密逻辑
     *
     * @return org.springframework.security.crypto.password.PasswordEncoder
     * @author wanglufei
     * @date 2022/4/11 6:32 PM
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义web相关的属性
     *
     * @param http
     * @author wanglufei
     * @date 2022/4/11 7:30 PM
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭CSRF
        http.csrf().disable()
                //授权
                .authorizeRequests()
                .antMatchers("/oauth/**", "/login/**", "/logout/**")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll();

    }
}

````
3. 自定义User实现UserDetails接口

```java
public class User implements UserDetails {
    private String username;
    private String password;
    private List<GrantedAuthority> authorities;//授权的


    public User(String username, String password, List<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```
4. 授权服务器的配置。用来对资源拥有者的身份进行认证、对访问资源进行授权。客户端要想访问资源需要通过认证服务器由资源拥有者授权后可访问。

```java
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
                .redirectUris("http:www.baidu.com")
                //作用域
                .scopes("all")
                //Grant_type  授权码模式
                .authorizedGrantTypes("authorization_code");
    }

}
```
5. 资源服务器的配置。通常为用户，也可以是应用程序，既该资源的拥有者。

```java
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
```
6. controller层主要是通过Authentication得到主体，也就是我们当前的user

```java
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
```
## 🥿实验结果
1. 测试

第一次运行，报错，显示SpringBoot和SpringCloud版本不兼容导致，所以要们降版本，要么升版本。
![image-20220411204301288](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112043460.png)

最终我降了SpringBoot版本到2.1.8.RELEASE，成功运行。

获取授权码，根据配置 

```http
http://localhost:8080/oauth/authorize?response_type=code&client_id=admin&redirect_url=http://www.baidu.com&scope=all
```
成功获取
![image-20220411205039785](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112050306.png)

登陆的时候，有遇到了一个问题
![](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112056182.png)
检查代码，在自定义的user并实现UserDetails的时候，isAccountNonLocked,改为true就可以了。后来控制台由显示密码为空，吭哧吭哧，有去改了user中的方法。终于进来了
![](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112145432.png)
<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112124345.png" alt="image-20220411212451486" style="zoom:50%;" />
![](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112144313.png)
终于拿到授权码了，5Z8hqu。

拿到授权码，我们再去授权服务器发送post请求，获取token，令牌。去PostMan发送POST请求

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112141005.png" alt="image-20220411214124887" style="zoom:50%;" />
<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112142165.png" alt="image-20220411214232114" style="zoom:50%;" />

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112144280.png" alt="image-20220411214402141" style="zoom:50%;" />

![image-20220411221958599](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112224500.png)

![image-20220411222016227](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112225210.png)

测试结束

## 👠SpringSecurity Oauth2的架构

![image-20220411181042001](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204112015357.png)

1. 用户访问,此时没有Token。Oauth2RestTemplate会报错，这个报错信息会被Oauth2ClientContextFiter捕获。并重定向到认证服务器

2. 认证服务器通过Authorization Endpoint进行授权，并通过AuthorizationServerTokenServices生成授权码并返回给客户端。

3. 客户端拿到授权码去认证服务器通过Token Endpoint调用AuthorizationServerTokenServices生成Token并返回给客户端。

# 👡SpringSecurityOAuth2整合Jwt

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204121619180.png" alt="image-20220412161347077" style="zoom:50%;" />

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204121619959.png" alt="image-20220412161625044" style="zoom:50%;" />



















