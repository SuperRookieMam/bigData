package com.yhl.oauthServer.component.config;


import com.yhl.oauthServer.component.feature.AuthenticationManagerConverter;
import com.yhl.oauthServer.component.feature.AuthorizationServerTokenService;
import com.yhl.oauthServer.component.feature.TokenStoreConverter;
import com.yhl.oauthServer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuthServerConfigurer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;

    @Autowired
    private UserApprovalService userApprovalService;

    @Autowired
    private OAthUserDetailesService userDetailesService;

    @Autowired
    private OAthGrantedAuthorityService oAthGrantedAuthorityService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 配置 oauth_client_details【client_id和client_secret等】
     * 信息的认证【检查ClientDetails的合法性】服务  设置 认证信息的来源：数据库 (可选项：数据库和内存,使用内存一般用来作测试)
     * 自动注入：ClientDetailsService的实现类参考 JdbcClientDetailsService 自定义实现
     * JdbcClientDetailsService (检查 ClientDetails 对象)
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(this.oAuthClientDetailsService);

    }

    /**
     * 配置：安全检查流程
     * 默认过滤器：BasicAuthenticationFilter
     * 1、oauth_client_details表中clientSecret字段加密【ClientDetails属性secret】
     * 2、CheckEndpoint类的接口 oauth/check_token 无需经过过滤器过滤，默认值：denyAll()
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 开启表单验证
        super.configure(security);
        // 通常情况下,Spring Security获取token的认证模式是基于http basic的,
        // 也就是client的client_id和client_secret是通过http的header或者url模式传递的，
        // 也就是通过http请求头的 Authorization传递，具体的请参考http basic
        // 或者http://client_id:client_secret@server/oauth/token的模式传递的
        // 当启用这个配置之后，server可以从表单参数中获取相应的client_id和client_secret信息
        // 默认情况下，checkToken的验证时denyAll的，需要手动开启
        security.checkTokenAccess("isAuthenticated()");
        security.allowFormAuthenticationForClients();
        security.passwordEncoder(getPassWordEncoder());

    }

    /**
     * 密码模式下配置认证管理器 AuthenticationManager,并且设置
     * AccessToken的存储介质tokenStore,如果不设置，则会默认使用内存当做存储介质。
     * 而该AuthenticationManager将会注入 2个Bean对象用以检查(认证)
     * 1、ClientDetailsService的实现类 JdbcClientDetailsService (检查 ClientDetails 对象)
     * 2、UserDetailsService的实现类 CustomUserDetailsService (检查 UserDetails 对象)
     */
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        AuthorizationServerTokenServices tokenService = tokenService();
        endpoints.tokenServices(tokenService);
        endpoints.userApprovalHandler(userApprovalHandler());
        endpoints.reuseRefreshTokens(false);
        // 如果要使用RefreshToken可用，必须指定UserDetailsService
        endpoints.userDetailsService(userDetailesService);
        endpoints.authenticationManager(((AuthorizationServerTokenService) tokenService).getAuthenticationManager());
    }

    @Bean
    public AuthorizationServerTokenServices tokenService() {
        // 自定义的验证token service  参考默认实现改写
        AuthorizationServerTokenService tokenService = new AuthorizationServerTokenService();
        tokenService.setTokenStore(tokenStore());
        // 自定义实现client 的架子，自定义实现clientdetaileService
        tokenService.setClientDetailsService(oAuthClientDetailsService);
        tokenService.setSupportRefreshToken(true);
        tokenService.setReuseRefreshToken(true); //允许使用刷新token，每次刷新的时候回去检查所访问的接口的资源api,
        // 自定义对用户用户权限的验证，参考默认实现
        tokenService.setAuthenticationManager(authenticationManager(tokenService));//自定义权限验证
        return tokenService;
    }

    @Bean
    public TokenStore tokenStore() {
        // 自定义token的增删改查
        TokenStoreConverter tokenStoreConverter = new TokenStoreConverter();
        return tokenStoreConverter;
    }

    @Bean
    public UserApprovalHandler userApprovalHandler() {
        // 存储用户的授权结果
        ApprovalStoreUserApprovalHandler handler = new ApprovalStoreUserApprovalHandler();
        handler.setApprovalStore(userApprovalService);
        handler.setRequestFactory(requestFactory());
        return handler;
    }

    @Bean
    public PasswordEncoder getPassWordEncoder() {
        // 使用系统的加密
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2RequestFactory requestFactory() {
        // 使用默认实现
        return new DefaultOAuth2RequestFactory(oAuthClientDetailsService);
    }


    public AuthenticationManager authenticationManager(AuthorizationServerTokenService tokenService) {
        // 自定义的权限管理
        AuthenticationManagerConverter manager = new AuthenticationManagerConverter();
        manager.setClientInfoService(oAuthClientDetailsService);
        manager.setTokenServices(tokenService);
        manager.setUserRoleService(userRoleService);
        manager.setOAthGrantedAuthorityService(oAthGrantedAuthorityService);
        return manager;
    }
}
