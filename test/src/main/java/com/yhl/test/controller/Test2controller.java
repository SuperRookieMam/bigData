package com.yhl.test.controller;

import com.yhl.base.controller.BaseController;
import com.yhl.test.entity.Test2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("test2")
public class Test2controller extends BaseController<Test2,Long> {
}
