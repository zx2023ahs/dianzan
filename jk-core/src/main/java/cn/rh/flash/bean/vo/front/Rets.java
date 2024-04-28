package cn.rh.flash.bean.vo.front;

public class Rets {

    public static final Integer SUCCESS = 20000;
    public static final Integer FAILURE = 9999;
    public static final Integer TOKEN_EXPIRE = 50014;

//    public static Ret success(Object data) {
//        return new Ret(Rets.SUCCESS, "成功", data);
//    }
    public static Ret success(Object data) {
        return new Ret(Rets.SUCCESS, "success", data);
    }

    public static Ret failure(String msg) {
        return new Ret(Rets.FAILURE, msg, null);
    }
    public static Ret failure(String msg,Object data) {
        return new Ret(Rets.FAILURE, msg, null);
    }

//    public static Ret success() {
//        return new Ret(Rets.SUCCESS, "成功", null);
//    }
    public static Ret success() {
        return new Ret(Rets.SUCCESS, "success", null);
    }

    public static Ret expire() {
        return new Ret(Rets.TOKEN_EXPIRE, "token 过期", null);
    }

    public static Ret expire(String message) {
        return new Ret(Rets.TOKEN_EXPIRE, message, null);
    }

    public static Ret apiExpire( String msg, Object data ) {
        return new Ret(Rets.TOKEN_EXPIRE, msg, data);
    }
}
