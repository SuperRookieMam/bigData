package com.yhl.system.componet.config;

import com.yhl.system.service.OAthUserDetailesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    OAthUserDetailesService oAthUserDetailesService;
    @Autowired
    private AuthenticationSuccessHandler loginSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler loginFailureHandler;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
             .authorizeRequests()
             .antMatchers("/login/**","/logout/**").permitAll()
             .anyRequest().authenticated()
             .and()
             .formLogin().successHandler(loginSuccessHandler).failureHandler(loginFailureHandler).permitAll(); //新增login form支持用户登录及授权;
        http.userDetailsService(oAthUserDetailesService);
//        检测倒用没有登陆跳转登陆页面重新拦截修改401错误
       http.exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    @Bean
    public PasswordEncoder getPassWordEncoder() {
        // 使用系统的加密
        return new BCryptPasswordEncoder();
    }
}
