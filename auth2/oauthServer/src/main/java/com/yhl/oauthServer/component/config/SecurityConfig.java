package com.yhl.oauthServer.component.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 需要保护/oauth/authorize以及/oauth/confirm_access
 * 这两个endpoint，当然主要是/oauth/authorize这个。
 * 由于其他几个/oauth/开头的认证endpoint
 * 配置的认证优先级高于默认的WebSecurityConfigurerAdapter配置(order=100)，
 * 因此默认的可以这样配置
 * 必须要吧/oauth/authorize ，/oauth/confirm_access
 * 保护起来否则就要报错
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                .requestMatchers().antMatchers("/oauth/**", "/login/**", "/logout/**")
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**").authenticated()
                .and()
                .formLogin().permitAll(); //新增login form支持用户登录及授权
    }

}
