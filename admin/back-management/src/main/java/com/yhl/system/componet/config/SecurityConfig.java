package com.yhl.system.componet.config;

import com.yhl.system.service.OAthUserDetailesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    OAthUserDetailesService oAthUserDetailesService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
             .authorizeRequests()
             .antMatchers("/login/**","/logout/**").permitAll()
             .anyRequest().authenticated()
             .and()
             .formLogin().permitAll(); //新增login form支持用户登录及授权;
        http.userDetailsService(oAthUserDetailesService);
    }

    @Bean
    public PasswordEncoder getPassWordEncoder() {
        // 使用系统的加密
        return new BCryptPasswordEncoder();
    }
}
