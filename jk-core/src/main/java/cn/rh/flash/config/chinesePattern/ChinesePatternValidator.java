package cn.rh.flash.config.chinesePattern;

import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.cache.ConfigCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class ChinesePatternValidator implements ConstraintValidator<ChinesePattern, String> {

    @Autowired
    private ConfigCache configCache;
    private String regexp;

    @Override
    public void initialize(ChinesePattern constraintAnnotation) {
        this.regexp = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // 空值视为验证通过
        }
        String enabledStr = configCache.get(ConfigKeyEnum.CHINESE_INPUT_PATTERN);
        boolean enabled = enabledStr != null && "1".equals(enabledStr.trim());
        if (!enabled) {
            return true; // 如果未启用中文字符检查，直接返回验证通过
        }
        // 进行正则表达式匹配
        return value.matches(regexp);
    }

}
