package com.yhl.oauthServer.controller;

import com.yhl.oauthServer.init.InitTable;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@RequestMapping("init")
public class InitController  {
    @Autowired
    InitTable initTable;


    @GetMapping("1")
    @ResponseBody
    @ApiOperation(value="根据实体插入", notes="insertByEntity")
    public  String getAccessToken() throws Exception {
        initTable.init();
        return "初始化完毕";
    }

}
