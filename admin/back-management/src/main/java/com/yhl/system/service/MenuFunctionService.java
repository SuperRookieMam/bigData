package com.yhl.system.service;

import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.service.BaseService;
import com.yhl.system.entity.MenuFunction;
import com.yhl.system.entity.OAthUserDetailes;

public interface MenuFunctionService extends BaseService<MenuFunction, Long> {
    ResultDto getMenuByUser(OAthUserDetailes oAthUserDetailes);
}
