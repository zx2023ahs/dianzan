package cn.rh.flash.bean.core;

import cn.rh.flash.core.aop.CheckValueValidated;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(value = RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Constraint(validatedBy = {CheckValueValidated.class})
public @interface CheckValue {


    /**
     * 是否需要（true:不能为空，false:可以为空）
     */
    public boolean isRequire() default false;

    /**
     * 字符串数组
     */
    public String[] strValues() default {};

    /**
     * int数组
     */
    public int[] intValues() default {};

    /**
     * 枚举类
     */
    public Class<?>[] enumClass() default {};

    String message() default "所传参数不在允许的值范围内";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
