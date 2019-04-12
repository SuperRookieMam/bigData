package com.yhl.resourceServer.componet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhl.resourceServer.componet.featur.AddAuthorizationCodeAccessTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.RedirectStrategy;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.TreeMap;

@Configuration
@EnableOAuth2Client
public class OAuth2ClientConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OAuth2ClientContextFilter oAuth2ClientContextFilter;

    /*
     *被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，
     * 类似于Serclet的inti()方法。被@PostConstruct修饰的方法会在构造函数之后，
     * init()方法之前运行。
     * */
    @PostConstruct//在构造之前做
    public void setFilter() {
        //为ClientContextFilter创建一个过滤器，这个filter 为oauth2的客户端实现了重定向策略
        oAuth2ClientContextFilter.setRedirectStrategy(new RedirectStrategy() {
            @Override
            public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
                    throws IOException {
                // 重新配置oauth2Context的重定向策略，不进行重定向，
                // 而是将重定向信息交由前端处理
                response.setStatus(HttpStatus.SEE_OTHER.value());
                TreeMap<String, String> parameterMap = new TreeMap<>();
                parameterMap.put("redirect_url", url);
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(objectMapper.writeValueAsString(parameterMap));
                }
            }
        });
    }

    /**
     * 回调函数，用于自定义用于获取用户详细信息的rest模板 通过OAuth2访问令牌完成。
     * 对于大多数提供程序来说，默认值应该没有问题，但是 偶尔您可能需要添加额外的拦截器，
     * 或更改请求  authenticator(这是令牌附加到发出请求的方式)。
     * 其余的 这里定制的模板是<i>only</i>用于内部执行
     * 认证(在SSO或资源服务器用例中)。
     */
    @Bean
    public UserInfoRestTemplateCustomizer userInfoRestTemplateCustomizer() {
        AccessTokenProviderChain provicerChain = new AccessTokenProviderChain(
                Arrays.<AccessTokenProvider>asList(
                        // 更换原有的AuthorizationCodeAccessTokenProvider
                        new AddAuthorizationCodeAccessTokenProvider(),
                        //这四个对应的auth 的授权方式，就是各自的实现拦截的方法，就是请求授权的方式
                        new ImplicitAccessTokenProvider(),
                        new ResourceOwnerPasswordAccessTokenProvider(),
                        new ClientCredentialsAccessTokenProvider()));

        return new UserInfoRestTemplateCustomizer() {
            @Override
            public void customize(OAuth2RestTemplate template) {
                template.setAccessTokenProvider(provicerChain);
            }
        };
    }
}
