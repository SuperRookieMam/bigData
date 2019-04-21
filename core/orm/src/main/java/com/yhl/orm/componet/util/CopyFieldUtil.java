package com.yhl.orm.componet.util;

import com.yhl.orm.componet.constant.FieldContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;

public class CopyFieldUtil {
   /* *//**
     * @params  entity 要跟新的实例
     * @param  fieldContext  等于 LinkedHashMap，接收的参数
     * @param entityManager  DAO层传入，主要作用时吧主要时吧引用类的ID转换成为对象
     * @params  map 所有的字段属性，注意时包括父类的字段，不知道看上线的方法
     * @return  T被设置后的实体
     * */
   /* public static <T> T copyPropertis(T entity, FieldContext fieldContext, EntityManager entityManager, Map<String, Field> map){
        //获取要跟新的所有字段
        Iterator iterator = fieldContext.keySet().iterator();
        try {
            while (iterator.hasNext()){
                String key =iterator.next().toString();
                // 判断主键不跟新
                if (map.containsKey(key+"_key")){
                    continue;
                }
                Object object = fieldContext.get(key);
                Field  field  = map.get(key);
                //如果字段类型可以转换成为传过来的类型说明时几大基础数据类型
                if (field.getType().isInstance(object)){
                    field.setAccessible(true);
                    field.set(entity,object);
                    field.setAccessible(false);
                }else {
                     *//*
                     * 因为多对一，时在此entity引用
                     * 一对多确实维护的另外张表，所以不做处理
                     * *//*
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
    }*/


    public static<T> void setFiled(FieldContext fieldContext,CriteriaUpdate<T> criteriaUpdate,EntityManager entityManager){
        //获取要跟新的所有字段
        Iterator iterator = fieldContext.keySet().iterator();
        Root<T> root =criteriaUpdate.getRoot();
        try {
            while (iterator.hasNext()){
                String key =iterator.next().toString();
                Assert.isTrue(!key.contains("."),"不支连表跟新");
                Path path = getPath(key,root);
                Class  cla =path.getJavaType();
                Object object = fieldContext.get(key);
                //如果字段类型可以转换成为传过来的类型说明时几大基础数据类型
                if (cla.isInstance(object)) {
                    criteriaUpdate.set(path,object);
                }else {
                    /*
                     * 自己的实体。如果引用的是自己引用的实体
                     * 跟新不支持"."最多是ManyToOne,OneToOne的引用修改
                     * 因为只有前端传过来的，引用类为ID和 引用类不一样所以
                     * */
                    if (!Collection.class.isAssignableFrom(path.getJavaType())&&
                         !Map.class.isAssignableFrom(path.getJavaType()) ){
                        Object obj =  entityManager.getReference(path.getJavaType(),object);
                        criteriaUpdate.set(path,obj);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Path getPath(String key,Root root){
        Assert.notNull(key, "key值不能为空");
        Path path =null;
        if (key.contains(".")) {
            String[] keys = StringUtils.split(key, ".");
            path = root.get(keys[0]);
            Class clazz = path.getJavaType();
            if (Set.class.isAssignableFrom(clazz)) {
                SetJoin setJoin = root.joinSet(keys[0]);
                for (int i = 1; i <keys.length ; i++) {
                    path = i==1?setJoin.get(keys[i]):path.get(keys[i]);
                }
            } else if (List.class.isAssignableFrom(clazz)) {
                ListJoin listJoin = root.joinList(keys[0]);
                for (int i = 1; i <keys.length ; i++) {
                    path = i==1?listJoin.get(keys[i]):path.get(keys[i]);
                }
            } else if (Map.class.isAssignableFrom(clazz)) {
                MapJoin mapJoin = root.joinMap(keys[0]);
                for (int i = 1; i <keys.length ; i++) {
                    path= i==1?mapJoin.get(keys[i]):path.get(keys[i]);
                }
            } else {
                //是many to one时
                for (int i = 1; i <keys.length ; i++) {
                    path = path.get(keys[i]);
                }
            }
        }else {//基本类型
            path = root.get(key);
        }
        return  path;
    }

}
