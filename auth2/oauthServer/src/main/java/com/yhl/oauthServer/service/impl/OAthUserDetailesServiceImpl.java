package com.yhl.oauthServer.service.impl;

import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.oauthCommon.entity.OAthUserDetailesDto;
import com.yhl.oauthServer.entity.OAthUserDetailes;
import com.yhl.oauthServer.service.OAthUserDetailesService;
import com.yhl.orm.componet.util.WhereBuildUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;
import java.util.List;

@Service
public class OAthUserDetailesServiceImpl extends BaseServiceImpl<OAthUserDetailes, Long> implements OAthUserDetailesService {
    private final String USERNAME = "userName";

    //注意user那么不是真的userName，自定义的userName为 userName-rolinfo
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("没找到用户名");
        }
        List<OAthUserDetailes> list = null;
        try {
            TypedQuery typedQuery = getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(USERNAME,username).and().end().buildTypedQuery();
            list =(List<OAthUserDetailes>)findbyTypeQuery(typedQuery).getData();
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException("没找到用户名");
        }
        if (list.isEmpty()) {
            throw new UsernameNotFoundException("没找到用户名");
        }

        return getOAthUserDetailesDto(new OAthUserDetailesDto(), list.get(0));
    }

    private OAthUserDetailesDto getOAthUserDetailesDto(OAthUserDetailesDto dto, OAthUserDetailes model) {
        dto.setCredentials(model.getCredentials());
        dto.setUserName(model.getUserName());
        dto.setExpired(model.isExpired());
        dto.setEnabled(model.isEnabled());
        dto.setPassWord(model.getPassWord());
        dto.setLock(model.isLock());
        dto.setHeadImage(model.getHeadImage());
        return dto;
    }


}
