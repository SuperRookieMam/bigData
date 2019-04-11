package com.yhl.orm.componet.constant;

import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
@Getter
@Setter
public class WhereContext  extends LinkedHashMap {
    private static final long serialVersionUID = 2999006637841047561L;
    //value = "当前页", dataType = "Integer", required = true
    private  Integer pageNum;
    //value = "分页大  小", dataType = "Integer", required = true
    private Integer pageSize;
      //因为排序时有序的所以用arry来接收
    private JSONArray sort; //排序对象[{'sortType': 'desc','fieldName':'fieldName'}]
    //按照顺序分组
    private JSONArray groupby;

    private ArrayList<Expression> expressions;

    private FieldContext fieldContext;
}
