package com.yhl.oauthServer.service.impl;

import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.oauthServer.entity.OAuthClientDetails;
import com.yhl.oauthServer.service.OAuthClientDetailsService;
import com.yhl.orm.componet.util.WhereBuildUtil;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;
import java.util.List;

@Service
public class OAuthClientDetailsServiceImpl extends BaseServiceImpl<OAuthClientDetails, Long> implements OAuthClientDetailsService {
    private final String CLIENTID = "clientId";

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        TypedQuery typedQuery =getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(CLIENTID,clientId).and().end().buildTypedQuery();
       List<OAuthClientDetails> clientDetails = (List<OAuthClientDetails>)findbyTypeQuery(typedQuery).getData();
        if (clientDetails.isEmpty()) {
            throw new ClientRegistrationException("客户端不存在");
        }
        return OAuthClientDetails.toOAuthClientDetailsDto(clientDetails.get(0));
    }


}
