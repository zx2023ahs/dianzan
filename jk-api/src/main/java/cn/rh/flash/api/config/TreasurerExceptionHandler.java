package cn.rh.flash.api.config;


import cn.rh.flash.api.interceptor.Requestxz.RequestLimitException;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.utils.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authz.AuthorizationException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.slf4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

@Log4j2
@RestControllerAdvice
public class TreasurerExceptionHandler {
    private static Logger LOGGER = getLogger(TreasurerExceptionHandler.class);

    public TreasurerExceptionHandler() {
    }



    /**
     * 校验错误拦截处理
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Ret validationBodyException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                LOGGER.warn("Data check failure : object={}, field={}, errorMessage={}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            });
            return Rets.failure( result.getFieldError().getDefaultMessage());
        }
        //其他错误
        return Rets.failure( result.getFieldError().getDefaultMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public Ret constraintViolationException(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        constraintViolations.forEach(p -> {
            ConstraintViolationImpl fieldError = (ConstraintViolationImpl) p;
            LOGGER.warn("Data check failure : object={}, field={}, errorMessage={}", fieldError.getRootBeanClass(), fieldError.getPropertyPath(), fieldError.getMessageTemplate());
        });
        //其他错误
        Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
        return Rets.failure( iterator.next().getMessageTemplate());
    }

    /**
     *
     * @return 错误信息
     */
    @ExceptionHandler(BindException.class)
    public Ret parameterTypeException(BindException exception) {
        BindingResult result = exception.getBindingResult();
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                LOGGER.warn("Data check failure : object={}, field={}, errorMessage={}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            });
            return Rets.failure( result.getFieldError().getDefaultMessage());
        }
        //其他错误
        return Rets.failure( result.getFieldError().getDefaultMessage());
    }

    /**
     * @return 服务端 请求有错误
     */
    @ExceptionHandler(ApplicationException.class)
    public Ret ApplicationException(ApplicationException exception) {
        LOGGER.warn("ApplicationException : errorMessage={}", exception.getLocalizedMessage() );
        return new Ret<>( exception.getCode(),exception.getMessage(),exception.getLocalizedMessage() );
    }

    /**
     * @return API 请求有错误
     */
    @ExceptionHandler(ApiException.class)
    public Ret ApiException(ApiException exception) {
        LOGGER.warn("ApiException : errorMessage={}", exception.getCode() );

        if( exception.getCode().equals( MessageTemplateEnum.TOKEN_EXPIRED.getCode() ) ){
            return Rets.expire(MessageTemplateEnum.TOKEN_EXPIRED.getCode());
        }
        return Rets.failure( exception.getCode(), exception );
    }

    /**
     * @return 空指针
     */
    @ExceptionHandler(NullPointerException.class)
    public void nullPointerException(NullPointerException exception) {
        if( exception.getStackTrace().length > 0 ){
            StackTraceElement stackTraceElement = exception.getStackTrace()[0];
            String className = stackTraceElement.getClassName();

            int lineNumber = stackTraceElement.getLineNumber();
            String fileName = stackTraceElement.getFileName();
            String methodName = stackTraceElement.getMethodName();
            log.error( String.format(" 空指针异常：【文件名：%s:%s; 方法名：%s.%s; 时间：%s 】 ",fileName,lineNumber,className,methodName, DateUtil.getTime() ) );
        }else{
            log.error( String.format(" 空指针异常：【时间：%s 】 ", DateUtil.getTime() ) );
        }
    }

    /**
     * 系统找不到指定的文件
     * @param e
     */
    @ExceptionHandler({FileNotFoundException.class})
    public void fileNotFoundException(FileNotFoundException e) {
        log.error( e.getMessage() );
    }

    @ExceptionHandler( MissingServletRequestParameterException.class )
    public Ret missingServletRequestParameterException(MissingServletRequestParameterException exception) {
        String message = exception.getMessage();
        return Rets.failure( message);
    }

    @ExceptionHandler(AuthorizationException.class)
    public Object handleAuthorizationException(AuthorizationException e) {
        LOGGER.error(e.getCause().getMessage(), e);
        return Rets.failure( "该操作未授权");
    }


    /**
     * 请求频繁 限制
     * @param e
     * @return
     */
    @ExceptionHandler(RequestLimitException.class)
    public Object handleRequestLimitException(RequestLimitException e) {
        return Rets.failure( MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT );
    }

}
