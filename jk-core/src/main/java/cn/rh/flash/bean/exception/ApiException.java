package cn.rh.flash.bean.exception;

import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装api的异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiException extends RuntimeException {

    private String code;
    private String name;
    private Object object;

    public ApiException(MessageTemplateEnum messageTemplateEnum) {
        this.code = messageTemplateEnum.getCode();
        this.name = messageTemplateEnum.getName();
        this.object = messageTemplateEnum;
    }

}
