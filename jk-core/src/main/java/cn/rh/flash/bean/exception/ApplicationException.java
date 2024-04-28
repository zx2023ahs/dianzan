package cn.rh.flash.bean.exception;

import lombok.Data;

/**
 * 封装系统的异常
 */
@Data
public class ApplicationException extends RuntimeException {

    private Integer code;
    private String message;

    public ApplicationException(ServiceExceptionEnum serviceExceptionEnum) {
        this.code = serviceExceptionEnum.getCode();
        this.message = serviceExceptionEnum.getMessage();
    }

}
