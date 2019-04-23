package com.yhl.system.service.Impl;

import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.orm.componet.util.PredicateBuilder;
import com.yhl.orm.componet.util.WhereBuilder;
import com.yhl.system.entity.OAthUserDetailes;
import com.yhl.system.service.OAthUserDetailesService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.TypedQuery;
import java.util.List;

@Service
public class OAthUserDetailesServiceImpl extends BaseServiceImpl<OAthUserDetailes,Long> implements OAthUserDetailesService {

    private final String USERNAME = "userName";

    //注意user那么不是真的userName，自定义的userName为 userName-rolinfo
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("没找到用户名");
        }
        List<OAthUserDetailes> list = null;
        try {
            WhereBuilder whereBuilder =getWhereBuilder();
            PredicateBuilder predicateBuilder =whereBuilder.getPredicateBuilder();
            TypedQuery typedQuery =  whereBuilder.where(
                    predicateBuilder.addEq(USERNAME,username).end())
                    .buildTypeQuery();
            list =(List<OAthUserDetailes>)findbyTypeQuery(typedQuery).getData();
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException("没找到用户名");
        }
        if (list.isEmpty()) {
            throw new UsernameNotFoundException("没找到用户名");
        }

        return   list.get(0);
    }
}
