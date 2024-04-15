package com.hahaha.platform.common.aspectj;

import com.hahaha.platform.common.enums.YesOrNoEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，给需要防止重复提交的方法加上该注解
 * @author 27707
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubmitRepeat {

    /**
     * 过期时间
     */
    long value() default 2L;

    /**
     * 拦截uri
     */
    String path() default "";

    /**
     * 拦截msg
     */
    String msg() default "请勿重复请求";

    /**
     * 是否抛异常
     */
    YesOrNoEnum exception() default YesOrNoEnum.YES;

}
