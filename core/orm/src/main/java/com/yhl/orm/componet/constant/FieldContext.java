package com.yhl.orm.componet.constant;


import java.util.LinkedHashMap;

public class FieldContext extends LinkedHashMap {
    private static final long serialVersionUID = 695153577520936945L;
    public FieldContext addField(String fieldName, Object value){
        this.put(fieldName,value);
    return  this;
    }
}
