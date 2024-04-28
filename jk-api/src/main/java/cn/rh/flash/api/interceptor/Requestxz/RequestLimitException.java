package cn.rh.flash.api.interceptor.Requestxz;

import org.springframework.core.NestedRuntimeException;

public class RequestLimitException extends NestedRuntimeException {

    public RequestLimitException(){
        super("访问太过频繁请稍后再试");
    }

    public RequestLimitException(String msg) {
        super(msg);
    }
}
