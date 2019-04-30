package com.yhl.create.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Component
public class MyClassUtil<T> {

    /**
     * 获取参数化的泛型的第一个参数类型
     * */
    public static  Class  getFirstClass(Class clazz){
        return getSuperClassGenricType(clazz,0);
    }

    /**
    * 通过反射获取定义class时申明父类的单行的类型
    * */
    public static  Class getSuperClassGenricType(Class clazz, int index){
       //得到泛型父类
       Type genType =clazz.getGenericSuperclass();

       //判断泛型是否参数化
       //如果没有参数话不支持泛型，直接返回obj
       if (!(genType instanceof ParameterizedType)){
            return  Object.class;
       }
       Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >=params.length||index<0){
            throw  new  RuntimeException("你输入的索引数组越界了,数组长度:"+params.length+",你的索引值");
        }
        if (!(params[index] instanceof  Class)){
            return Object.class;
        }
       return (Class) params[index];
   }
    /**
     * 判断一个类是自定义类
     * @return  true 自定义类，false java核心类
     * */
   public  static  boolean   isCustomClass(Class clazz){
        //如果时java核心类clazz.getClassLoader()==null,类加载器的基础知识，自己取了解
        return clazz!=null&&clazz.getClassLoader()!=null;
   }
   /**
    * 根据字段名反射获取字段
    * @param  object 实例;
    * @param  name 字段名
    * @return  返回属性值
    * */
   public  static  Object getFieldByName(Object object,String name ){
       Class clazz =object.getClass();
       Object object1=null;
       try {
           Field field = clazz.getDeclaredField(name);
           field.setAccessible(true);
           object1 = field.get(object);
           field.setAccessible(false);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return  object1;
   }
    /**
     * 根据字段名反射获取字段
     * @param  object 实例;
     * @param  name 字段名
     * @param  value 要设置的字段值
     * @return  返回属性值
     * */
    public  static  void setFieldByName(Object object,String name,Object value ){
        Class clazz =object.getClass();
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(object,value);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  Class getClassForName(String name){
       Class clazz=null;
       try {
           clazz = Class.forName(name);
       }catch (Exception e){
           e.printStackTrace();
       }
        return  clazz;
    }
    /**
     *获取父类
     * */
    public static  Class getSuperClass(String className){
        Class superClazz=null;
        Class clazz =  getClassForName(className);
        superClazz=  clazz.getSuperclass();
        return  superClazz;
    }

    /**
     * 获取包括父类的所有字段,如果父类有重名的则返回的子类的字段名字
     * @param clazz 要获取字段的类
     * @return  Map<字段名,Field>
     * */
    public static List<Field> getAllFields(Class clazz){
        Class superClass =clazz.getSuperclass();
        List<Field> list =new ArrayList<>();
        boolean flag =superClass!=null;
        while (flag){
            list.addAll(Arrays.asList(superClass.getDeclaredFields()));
            superClass =superClass.getSuperclass();
            flag =superClass!=null;
        }
        list.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return list;
    }
}
