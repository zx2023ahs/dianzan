package cn.rh.flash.bean.core;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ApiLog {
    /**
     * 登录版本
     */
    String version() default "v1";
    String logoin() default "login";

}
