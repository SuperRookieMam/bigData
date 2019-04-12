package com.yhl.base.componet.util;

import com.alibaba.fastjson.JSONObject;
import com.yhl.orm.componet.constant.WhereContext;

import java.util.Map;

public class ParamUtil {
    public static  final  String WHERECONTEXT ="whereContext";

    public static WhereContext getWhereContext(Map<String,Object> map){
        return map.containsKey(WHERECONTEXT)
                ?JSONObject.parseObject( map.get("whereContext").toString(),WhereContext.class)
                :null;



    }
}
