package com.yhl.system.controller;

import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.controller.BaseController;
import com.yhl.system.componet.annotation.CurrentUser;
import com.yhl.system.entity.Company;
import com.yhl.system.entity.OAthUserDetailes;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("company")
public class CompanyController extends BaseController<Company,Long> {


    @GetMapping("testuser")
    @ApiOperation(value="测试注解", notes="testuser")
    public ResultDto testuser(@CurrentUser OAthUserDetailes oAthUserDetailes){
        return  ResultDto.success(oAthUserDetailes);
    }
}
