package com.yhl.system.service.Impl;

import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.orm.componet.util.PredicateBuilder;
import com.yhl.orm.componet.util.WhereBuilder;
import com.yhl.system.entity.MenuFunction;
import com.yhl.system.entity.MenuFunctionRole;
import com.yhl.system.entity.OAthUserDetailes;
import com.yhl.system.entity.UserRole;
import com.yhl.system.service.MenuFunctionRoleService;
import com.yhl.system.service.MenuFunctionService;
import com.yhl.system.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuFunctionServiceImpl extends BaseServiceImpl<MenuFunction, Long> implements MenuFunctionService {
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private  MenuFunctionRoleService menuFunctionRoleService;
    private  static  long COMANYID=1l;
    @Override
    public ResultDto getMenuByUser(OAthUserDetailes oAthUserDetailes){
        if (ObjectUtils.isEmpty(oAthUserDetailes)){
            return ResultDto .success(Collections.emptyList());
        }
       //查询此用户所在公司的角色
       WhereBuilder<UserRole,Long> whereBuilder = userRoleService.getWhereBuilder();
       PredicateBuilder pre= whereBuilder.getPredicateBuilder();
       UserRole userRole =whereBuilder.where(
                                            pre.addEq("userName",oAthUserDetailes.getUsername())
                                                .and()
                                                .addEq("companyId",COMANYID).end())
                                                 .buildTypeQuery().getSingleResult();
        if (ObjectUtils.isEmpty(oAthUserDetailes)){
            return ResultDto .success(Collections.emptyList());
        }
        //获取此用户的角色拥有的菜单
        WhereBuilder<MenuFunctionRole,Long> whereBuilder1 = menuFunctionRoleService.getWhereBuilder();
        PredicateBuilder pre1 = whereBuilder1.getPredicateBuilder();
        List<MenuFunctionRole> list= whereBuilder1.where(
                                                pre1.addEq("roleId",userRole.getRoleInfo().getId()).end(),
                                                pre1.addEq("companyId",COMANYID).end()
                                            ).buildTypeQuery().getResultList();
        Long[] menuIds = list.stream().map(ele-> ele.getMenuFunctionId()).collect(Collectors.toList()).toArray(new Long[list.size()]);

        //查询出对应的菜单并排序
        WhereBuilder<MenuFunction,Long> whereBuilder2 =getWhereBuilder();
        PredicateBuilder pre2 = whereBuilder2.getPredicateBuilder();
        List<MenuFunction> list1 = whereBuilder2
                                         .where(
                                                 pre2.addIn("id",menuIds)
                                                     .and()
                                                     .addEq("isShow",1).end()
                                             )
                                         .orderby( "pid:asc", "sort:asc")
                                         .buildTypeQuery().getResultList();

       return ResultDto.success(combinationMemu(list1,0));
    }
    private List<Map<String,Object>>  combinationMemu(List<MenuFunction> list,int pid){
        List<Map<String,Object>> list1 =new ArrayList<>();
        Iterator<MenuFunction> iterator =  list.iterator();
         while (iterator.hasNext()){
             MenuFunction ele =iterator.next();
             if (ele.getPid()==pid){
                 Map<String,Object> map =new HashMap<>();
                 map.put("cname",ele.getCname());
                 map.put("url",ele.getUrl());
                 map.put("id",ele.getId());
                 map.put("isMenu",ele.getIsMenu());
                 iterator.remove();
                 map.put("submenus",combinationMemu(list,ele.getFunctionNumber()));
                 list1.add(map);
             }
         }
        return list1;
    }


}
