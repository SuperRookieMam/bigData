package com.yhl.resourceServer.service.impl;

import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.orm.componet.util.PredicateBuilder;
import com.yhl.orm.componet.util.WhereBuilder;
import com.yhl.resourceServer.entity.OAuthClientDetails;
import com.yhl.resourceServer.service.OAuthClientDetailsService;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;
import java.util.List;

@Service("OAuthClientDetailsServiceImpls")
public class OAuthClientDetailsServiceImpl extends BaseServiceImpl<OAuthClientDetails, String> implements OAuthClientDetailsService {
    private final String CLIENTID = "clientId";
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        WhereBuilder whereBuilder =getWhereBuilder();
        PredicateBuilder predicateBuilder =whereBuilder.getPredicateBuilder();

        TypedQuery typedQuery =whereBuilder.where(
                        predicateBuilder.addEq(CLIENTID, clientId).end()
                                     ).buildTypeQuery();
        List<OAuthClientDetails> clientDetails = (List<OAuthClientDetails>) findbyTypeQuery(typedQuery).getData();
        if (clientDetails.isEmpty()) {
            throw new ClientRegistrationException("客户端不存在");
        }
        return OAuthClientDetails.toOAuthClientDetailsDto(clientDetails.get(0));
    }


}
