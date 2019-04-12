package com.yhl.resourceServer.componet.featur;

import com.yhl.securityCommon.access.RequestAuthoritiesAccessDecisionVoter;
import com.yhl.securityCommon.access.RequestAuthorityAttribute;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;

public class RequestAuthoritiesAccessDecisionVoterImpl implements RequestAuthoritiesAccessDecisionVoter {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof RequestAuthorityAttribute;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    /**
     */
    @Override
    public int vote(Authentication authentication, FilterInvocation object, Collection<ConfigAttribute> attributes) {
        RequestAuthorityAttribute  Attribute =(RequestAuthorityAttribute)attributes.iterator().next();
         return  Attribute.isAccessVisit()? ACCESS_GRANTED : ACCESS_DENIED ;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
      //  Assert.state(tokenStoreResourceServerTokenServices != null, "tokenStoreResourceServerTokenServices are required");
    }

}
