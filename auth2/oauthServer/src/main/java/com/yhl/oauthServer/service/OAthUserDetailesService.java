package com.yhl.oauthServer.service;

import com.yhl.base.service.BaseService;
import com.yhl.oauthServer.entity.OAthUserDetailes;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface OAthUserDetailesService extends UserDetailsService, BaseService<OAthUserDetailes, Long> {
}
