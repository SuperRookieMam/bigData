package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.ClientRegisteredRedirectUri;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRegisteredRedirectUriDao extends BaseDao<ClientRegisteredRedirectUri, Long> {
}
