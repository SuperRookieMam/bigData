package com.yhl.orm.componet.constant;


import com.yhl.orm.componet.util.MyClassUtil;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class UpdateFields extends LinkedHashMap {

    public  UpdateFields addField(String fieldName,Object value){
        this.put(fieldName,value);
    return  this;
    }
    /**
     * @param  entity 要跟新的实例
     * @param  updateFields  等于 LinkedHashMap，接收的参数
     * @param entityManager  DAO层传入，主要作用时吧主要时吧引用类的ID转换成为对象
     * @return  T被设置后的实体
     *  //TODO 注意：不管主键的名字交什么,UpdateParams中代表主键的就是id
     * */
    public static <T> T copyPropertis(T entity, UpdateFields updateFields, EntityManager entityManager){
        //获取要跟新的所有字段
        Map<String, Field> map = MyClassUtil.getAllFields(entity.getClass());
        Iterator iterator = updateFields.keySet().iterator();
        try {
            while (iterator.hasNext()){
                String key =iterator.next().toString();
                if ("id".equals(key)){
                    continue;
                }
                Object object = updateFields.get(key);
                Field  field  = map.get(key);
                //如果字段类型可以转换成为传过来的类型说明时极大基础数据类型
                if (field.getType().isInstance(object)){
                    field.setAccessible(true);
                    field.set(entity,object);
                    field.setAccessible(false);
                }else {
                    /*
                     * 因为多对一，时在此entity引用
                     * 一对多确实维护的另外张表，所以不做处理
                     * */
                    if (field.getAnnotation(ManyToOne.class)!=null){
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
    /**
     * @param  entity 要跟新的实例
     * @param  updateFields  等于 LinkedHashMap，接收的参数
     * @param entityManager  DAO层传入，主要作用时吧主要时吧引用类的ID转换成为对象
     * @param  map 所有的字段属性，注意时包括父类的字段，不知道看上线的方法
     * @return  T被设置后的实体
     *  //TODO 注意：不管主键的名字交什么,UpdateParams中代表主键的就是id
     * */
    public static <T> T copyPropertis(T entity, UpdateFields updateFields, EntityManager entityManager, Map<String, Field> map){
        //获取要跟新的所有字段
        Iterator iterator = updateFields.keySet().iterator();
        try {
            while (iterator.hasNext()){
                String key =iterator.next().toString();
                if ("id".equals(key)){
                    continue;
                }
                Object object = updateFields.get(key);
                Field  field  = map.get(key);
                //如果字段类型可以转换成为传过来的类型说明时极大基础数据类型
                if (field.getType().isInstance(object)){
                    field.setAccessible(true);
                    field.set(entity,object);
                    field.setAccessible(false);
                }else {
                    /*
                     * 因为多对一，时在此entity引用
                     * 一对多确实维护的另外张表，所以不做处理
                     * */
                    if (field.getAnnotation(ManyToOne.class)!=null){
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
