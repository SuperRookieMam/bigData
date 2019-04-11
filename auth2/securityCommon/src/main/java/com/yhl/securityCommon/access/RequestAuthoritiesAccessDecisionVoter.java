package com.yhl.securityCommon.access;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.web.FilterInvocation;

/**
 * 资源服务对客户端器验证token的自定义验证
 * 其实就是一个验证规则，暂时就一个吧，以后照倒这个加
 */
public interface RequestAuthoritiesAccessDecisionVoter extends AccessDecisionVoter<FilterInvocation>, InitializingBean {


}
