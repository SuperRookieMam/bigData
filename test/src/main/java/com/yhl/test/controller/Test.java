package com.yhl.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class Test {

    @RequestMapping("/hello")
    public String hello(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        System.out.println("参数数量："+parameterMap.size());
        /**这三个参数是比较有用的参数
         tcurl:rtmp地址
         name:"rtmp://ngnix服务器地址/live/id"地址中去除推流地址的最后一个参数，可传入直播用户ID
         type:推流类型
         */
        System.out.println("tcurl:"+request.getParameter("tcurl"));
        System.out.println("name:"+request.getParameter("name"));
        System.out.println("type:"+request.getParameter("type"));
        //打印所有回调传过来的参数
        for(Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            System.out.println(entry.getKey() + "====================");
            for(String str : entry.getValue()) {
                System.out.println("\t"+str);
            }
        }

        return "hello world2";
    }
}
