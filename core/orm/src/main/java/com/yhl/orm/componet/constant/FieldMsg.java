package com.yhl.orm.componet.constant;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter
@Setter
public class FieldMsg  {
    private String fieldName ;
    private String connection;
    private Object value;
    public FieldMsg(){
    }
   public   FieldMsg(String fieldName,String connection,Object value){
        this.fieldName =fieldName;
        this.connection =connection;
        this.value =value;
   }
}
