package cn.rh.flash.core.aop;

import cn.rh.flash.bean.core.CheckValue;
import com.google.common.collect.Sets;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CheckValueValidated implements ConstraintValidator<CheckValue, Object>{
    private boolean isRequire;
    private Set<String> strValues;
    private List<Integer> intValues;

    @Override
    public void initialize(CheckValue constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        strValues = Sets.newHashSet(constraintAnnotation.strValues());
        intValues = Arrays.stream(constraintAnnotation.intValues()).boxed().collect(Collectors.toList());
        isRequire = constraintAnnotation.isRequire();

        //将枚举类的name转小写存入strValues里面，作为校验参数
        Optional.ofNullable(constraintAnnotation.enumClass()).ifPresent(e -> Arrays.stream(e).forEach(
                c -> Arrays.stream(c.getEnumConstants()).forEach(v -> strValues.add(v.toString().toLowerCase()))
        ));
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null && !isRequire) {
            return true;
        }

        if (value instanceof String) {
            return strValues.contains(value);
        }
        if (value instanceof Integer) {
            return intValues.stream().anyMatch(e -> e.equals(value));
        }

        return false;
    }

}
