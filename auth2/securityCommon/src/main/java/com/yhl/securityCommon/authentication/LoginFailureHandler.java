package com.yhl.securityCommon.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class LoginFailureHandler implements AuthenticationFailureHandler, InitializingBean {

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String result = objectMapper.writeValueAsString(exception.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(result);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.isNull(objectMapper)) {
            objectMapper = new ObjectMapper();
        }
    }

}
