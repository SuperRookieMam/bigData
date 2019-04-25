package com.yhl.create.componet.annotation;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Description {

    //列表头名字
    String headerName() default "";

    //update跟新的时列对应的类型. select checkbox  time input
    String eleType() default "";

    boolean changeToBoolen() default false;
}
