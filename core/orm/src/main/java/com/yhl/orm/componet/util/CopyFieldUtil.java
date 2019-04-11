package com.yhl.orm.componet.util;

import com.yhl.orm.componet.constant.FieldContext;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

public class CopyFieldUtil {
    /**
     * @param  entity 要跟新的实例
     * @param  fieldContext  等于 LinkedHashMap，接收的参数
     * @param entityManager  DAO层传入，主要作用时吧主要时吧引用类的ID转换成为对象
     * @param  map 所有的字段属性，注意时包括父类的字段，不知道看上线的方法
     * @return  T被设置后的实体
     * */
    public static <T> T copyPropertis(T entity, FieldContext fieldContext, EntityManager entityManager, Map<String, Field> map){
        //获取要跟新的所有字段
        Iterator iterator = fieldContext.keySet().iterator();
        try {
            while (iterator.hasNext()){
                String key =iterator.next().toString();
                // 判断主键不跟新
                if (map.containsKey(key+"_key")){
                    throw new  RuntimeException("主键不能跟新");
                }
                Object object = fieldContext.get(key);
                Field  field  = map.get(key);
                //如果字段类型可以转换成为传过来的类型说明时几大基础数据类型
                if (field.getType().isInstance(object)){
                    field.setAccessible(true);
                    field.set(entity,object);
                    field.setAccessible(false);
                }else {
                    /*
                     * 因为多对一，时在此entity引用
                     * 一对多确实维护的另外张表，所以不做处理
                     * */
                    if (field.getAnnotation(ManyToOne.class)!=null
                            ||field.getAnnotation(OneToOne.class)!=null){
                        Object obj =  entityManager.getReference(field.getType(),object);
                        field.setAccessible(true);
                        field.set(entity,obj);
                        field.setAccessible(false);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  entity;
    }
}
