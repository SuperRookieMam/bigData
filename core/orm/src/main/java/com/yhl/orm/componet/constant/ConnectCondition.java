package com.yhl.orm.componet.constant;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class ConnectCondition  implements Serializable {

    private JSONObject like;//{field.field：value}
    private JSONObject notLike;
    private JSONObject lt;
    private JSONObject le;
    private JSONObject gt;
    private JSONObject ge;
    private JSONObject eq;
    private JSONObject notEq;
    private JSONObject in; //{"字段名":[]}
    private JSONObject notIn;
    private JSONObject isNull;
    private JSONObject isNotNull;
    private JSONObject isMember;
    private JSONObject isNotMember;

    public ConnectCondition addLike(String key,Object value){
        like =like==null?new JSONObject():like;
        like.put(key,value);
        return this;
    }
    public ConnectCondition addNotLike(String key,Object value){
        notLike =notLike==null?new JSONObject():notLike;
        notLike.put(key,value);
        return this;
    }
    public ConnectCondition addLt(String key,Object value){
        lt =lt==null?new JSONObject():lt;
        lt.put(key,value);
        return this;
    }
    public ConnectCondition addLe(String key,Object value){
        le =le==null?new JSONObject():le;
        le.put(key,value);
        return this;
    }
    public ConnectCondition addGt(String key,Object value){
        gt =gt==null?new JSONObject():gt;
        gt.put(key,value);
        return this;
    }
    public ConnectCondition addGe(String key,Object value){
        ge =ge==null?new JSONObject():ge;
        ge.put(key,value);
        return this;
    }
    public ConnectCondition addEq(String key,Object value){
        eq =eq==null?new JSONObject():eq;
        eq.put(key,value);
        return this;
    }
    public ConnectCondition addNotEq(String key,Object value){
        notEq =notEq==null?new JSONObject():notEq;
        notEq.put(key,value);
        return this;
    }

    public ConnectCondition addIn(String key,Object[] value){
        in =in==null?new JSONObject():in;
        in.put(key,value);
        return this;
    }
    public ConnectCondition addNotIn(String key,Object[] value){
        notIn =notIn==null?new JSONObject():notIn;
        notIn.put(key,value);
        return this;
    }
    public ConnectCondition addIsNull(String key){
        isNull =isNull==null?new JSONObject():isNull;
        isNull.put(key,"");
        return this;
    }
    public ConnectCondition addIsNotNull(String key){
        isNotNull =isNotNull==null?new JSONObject():isNotNull;
        isNotNull.put(key,"");
        return this;
    }

    public ConnectCondition addIsMember(String key,Object[] value){
        isMember =isMember==null?new JSONObject():isMember;
        isMember.put(key,value);
        return this;
    }
    public ConnectCondition addIsNotMember(String key,Object[] value){
        isNotMember =isNotMember==null?new JSONObject():isNotMember;
        isNotMember.put(key,value);
        return this;
    }


}
