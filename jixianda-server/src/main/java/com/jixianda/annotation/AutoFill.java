package com.jixianda.annotation;

import com.jixianda.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通过自定义注解，用于标识某个方法的进行公共字段填充
 * @author huanzi
 * @date 2025/12/14 16:01
 */
//意思是放在方法上面生效
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoFill {
    //数据库操作类型，update,insert
    OperationType value();

}
