package com.yhl.test.controller;

import com.yhl.base.controller.BaseController;
import com.yhl.test.entity.Test1;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("test1")
public class Test1controller extends BaseController<Test1,Long> {

}
