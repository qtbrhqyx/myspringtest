package com.lagou.edu.zuoye.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MyTransactional{
    String value() default "";
    boolean proxy() default true;
}
