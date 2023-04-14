package com.graduation.medicaltaskscheduled.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author RabbitFaFa
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {
    //操作类型
    String operateType() default "";
    //操作描述
    String operateDesc() default "";
    //更新的新内容的表的id(操作类型为MODIFY时记录)
    String contentId() default "";
    //用户类型(0-管理员 1-医生 2-患者)
    String userType() default "0";
}
