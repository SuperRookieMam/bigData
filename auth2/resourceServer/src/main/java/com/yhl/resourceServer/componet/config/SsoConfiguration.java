package com.yhl.resourceServer.componet.config;


import com.yhl.resourceServer.componet.featur.OAuthAccessTokenConverter;
import com.yhl.resourceServer.componet.featur.RemoteTokenServicesConvertre;
import com.yhl.resourceServer.componet.featur.UserDetailsAuthenticationConverter;
import com.yhl.resourceServer.componet.featur.UserDetailsDtoPrincipalExtractor;
import com.yhl.securityCommon.authentication.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.PostConstruct;
import java.security.Principal;

/**
 * 配置zuul网关服务的单点登录
 *
 * @author LiDong
 */
@Configuration
@EnableConfigurationProperties(OAuth2SsoProperties.class)
@Import({ResourceServerTokenServicesConfiguration.class})
public class SsoConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private FilterSecurityInterceptor filterSecurityInterceptor;

    private RemoteTokenServicesConvertre remoteTokenServices;

    @Autowired
    private OAuth2SsoProperties ssoProperties;
     //Factory used to create the {@link OAuth2RestTemplate}
    // used for extracting user info during authentication if none is available.
    @Autowired
    private UserInfoRestTemplateFactory restTemplateFactory;

    /*	@Autowired*//*trategy used to handle a successful user authentication*//*
	private AuthenticationSuccessHandler loginSuccessHandler;*/

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**").authorizeRequests().anyRequest().authenticated();
        // 指定相关资源的权限校验过滤器
        http.addFilterBefore(filterSecurityInterceptor, FilterSecurityInterceptor.class);
        http.csrf()
                .disable();
        http.exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
        //此security使用在outheclient 的配置上
        http.apply(new OAuth2ClientAuthenticationConfigurer(oauth2SsoFilter()));
    }

    /**
     * 如果使用/oauth/check_token 端点解析用户信息，
     * 需要额外的转换来处理{@link UserDetails}信息
     *
     * @return
     */
    @Bean
    public AccessTokenConverter accessTokenConverter() {
        OAuthAccessTokenConverter tokenConverter = new OAuthAccessTokenConverter();
        tokenConverter.setUserAuthenticationConverter(userTokenConverter());
        return tokenConverter;
    }

    /**
     * 配置一个Converter，使之可以解析token_info中的Pripical<br />
     * 此处将 {@link Principal}转换为一个
     */
    @Bean
    public UserAuthenticationConverter userTokenConverter() {
        UserDetailsAuthenticationConverter authenticationConverter = new UserDetailsAuthenticationConverter();
        authenticationConverter.setPrincipalExtractor(principalExtractor());
        return authenticationConverter;
    }

    /**
     * 指定一个用户信息解码器，
     * 将从服务器获取过来的用户信息解码为本地
     * {@link com.yhl.oauthCommon.entity.OAthUserDetailesDto}
     */
    @Bean
    public PrincipalExtractor principalExtractor() {
        return new UserDetailsDtoPrincipalExtractor();
    }

    /**
     * 其实这里可以自定义filter
     * 继承AbstractAuthenticationProcessingFilter
     * 来处理自己需要的逻辑
     * 这里配置的遇见login拦截
     */
    private OAuth2ClientAuthenticationProcessingFilter oauth2SsoFilter() {
        //拦截/login
        OAuth2ClientAuthenticationProcessingFilter filter =
                new OAuth2ClientAuthenticationProcessingFilter(ssoProperties.getLoginPath());
        //这里配置配置的远程获取user信息的，如果要自定义也可以
        filter.setRestTemplate(restTemplateFactory.getUserInfoRestTemplate());
        //这里配置远程定获取token的servicve貌似有默认实现
        filter.setTokenServices(remoteTokenServices);

        //applicationContext context
        filter.setApplicationEventPublisher(getApplicationContext());

        //这里配置登录成功后的控制器
        filter.setAuthenticationSuccessHandler(loginSuccessHandler());

        return filter;
    }

    private AuthenticationSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler();
    }

    /**
     * 指定解码Token信息的解码器
     */
    @PostConstruct
    public void config() {
        remoteTokenServices =RemoteTokenServicesConvertre();
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
    }

    public  RemoteTokenServicesConvertre RemoteTokenServicesConvertre(){
        RemoteTokenServicesConvertre remoteTokenServicesConvertre =new RemoteTokenServicesConvertre();
        remoteTokenServicesConvertre.setResourceId(1l);
        remoteTokenServicesConvertre.setResourceSecret("123456");
        remoteTokenServicesConvertre.setUrl("http://127.0.0.1:8002/tokenGet/token");
        remoteTokenServicesConvertre.setCheckTokenEndpointUrl("http://127.0.0.1:8002/oauth/check_token");
        remoteTokenServicesConvertre.setClientId("zuul");
        remoteTokenServicesConvertre.setClientSecret("123456");
        return remoteTokenServicesConvertre ;
    }


    // 这配置覆盖
    private static class OAuth2ClientAuthenticationConfigurer
            extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

        private OAuth2ClientAuthenticationProcessingFilter filter;

        OAuth2ClientAuthenticationConfigurer(OAuth2ClientAuthenticationProcessingFilter filter) {
            this.filter = filter;
        }
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            OAuth2ClientAuthenticationProcessingFilter ssoFilter = this.filter;
            ssoFilter.setSessionAuthenticationStrategy(
                    builder.getSharedObject(SessionAuthenticationStrategy.class));
            builder.addFilterAfter(ssoFilter,
                    AbstractPreAuthenticatedProcessingFilter.class);
        }

    }
}
