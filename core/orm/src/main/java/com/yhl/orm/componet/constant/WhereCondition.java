package com.yhl.orm.componet.constant;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
public class WhereCondition implements Serializable {

    //value = "当前页", dataType = "Integer", required = true
    private  Integer pageNum;

    //value = "分页大  小", dataType = "Integer", required = true
    private Integer pageSize;

    private ConnectCondition and;

    private ConnectCondition or;
    //因为排序时有序的所以用arry来接收
    private JSONArray sort; //排序对象[{'sortType': 'desc','fieldName':'fieldName'}]
    //按照顺序分组
    private JSONArray groupby;// ['fieldName1','fieldName']

    public  ConnectCondition and(){
        and=and==null?new ConnectCondition():and;
        return and;
    }

    public  ConnectCondition or(){
        or=or==null?new ConnectCondition():or;
        return or;
    }

    public  WhereCondition addSort(String key,String sortType){
        sort=sort==null?new JSONArray():sort;
        if ("desc".equalsIgnoreCase(sortType)||"asc".equalsIgnoreCase(sortType)){
            JSONObject jsonObject =new JSONObject();
            jsonObject.put("fieldName",key);
            jsonObject.put("sortType",sortType);
            sort.add(jsonObject);
        }
        return this;
    }

    public  WhereCondition addgroupby(String fieldName){
        groupby=groupby==null?new JSONArray():groupby;
        if (!StringUtils.isEmpty(fieldName)){
            groupby.add(fieldName) ;
        }
        return this;
    }
}
