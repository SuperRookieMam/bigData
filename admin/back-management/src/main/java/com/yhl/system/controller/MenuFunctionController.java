package com.yhl.system.controller;

import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.controller.BaseController;
import com.yhl.system.componet.annotation.CurrentUser;
import com.yhl.system.entity.MenuFunction;
import com.yhl.system.entity.OAthUserDetailes;
import com.yhl.system.service.MenuFunctionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("menuFunction")
public class MenuFunctionController extends BaseController<MenuFunction,Long> {

    @Autowired
    private MenuFunctionService menuFunctionService;
    /**
     * 根据参数自定义查询
     * */
    @GetMapping("menus")
    @ResponseBody
    @ApiOperation(value="根据登陆的用户获取菜单", notes="getMenuByUser")
    public ResultDto getMenuByUser(@CurrentUser  OAthUserDetailes oAthUserDetailes){
        return menuFunctionService.getMenuByUser(oAthUserDetailes);
    }
}
