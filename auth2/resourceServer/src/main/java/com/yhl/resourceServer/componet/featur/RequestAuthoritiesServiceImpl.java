package com.yhl.resourceServer.componet.featur;

import com.yhl.oauthCommon.entity.OAthGrantedAuthorityDto;
import com.yhl.oauthCommon.entity.OAuthAccessTokenDto;
import com.yhl.securityCommon.access.RequestAuthorityAttribute;
import com.yhl.securityCommon.provider.RequestAuthoritiesService;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Setter
public class RequestAuthoritiesServiceImpl implements RequestAuthoritiesService, InitializingBean {

    private final String ACCESS_TOKEN="access_token";

    private ResourceServerTokenServices resourceServerTokenServices;

    // 获取这个token能访问的这个对应的客户端的信息路径，以及请求方式信息
    @Override
    public List<RequestAuthorityAttribute> listAllAttributes(String token) {
        List<RequestAuthorityAttribute> list = new ArrayList<>();
        OAuthAccessTokenDto oAuthAccessTokenDto =(OAuthAccessTokenDto)resourceServerTokenServices.readAccessToken(token);
        Set<OAthGrantedAuthorityDto> set = oAuthAccessTokenDto.getOAthGrantedAuthorities();
        Iterator<OAthGrantedAuthorityDto> iterator =set.iterator();
        while (iterator.hasNext()){
            OAthGrantedAuthorityDto dto =iterator.next();
            list.add(new RequestAuthorityAttribute(dto.getApiUri(),dto.getMethod(),dto.getMactherType()));
        }
        return list;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(resourceServerTokenServices, "resourceServerTokenServices must be set");
    }
}
