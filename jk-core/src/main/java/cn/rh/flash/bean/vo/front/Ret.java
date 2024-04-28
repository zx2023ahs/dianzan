package cn.rh.flash.bean.vo.front;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "Ret", description = "结果集")
public class Ret<T> {
    @ApiModelProperty("状态码 [ SUCCESS = 20000; FAILURE = 9999; TOKEN_EXPIRE = 50014]")
    private Integer code;
    @ApiModelProperty("消息")
    private String msg;
    @ApiModelProperty( value = "返回数据")
    private T data;
    @ApiModelProperty("状态 true/false")
    private boolean success;

    public Ret() {

    }

    public Ret(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = Rets.SUCCESS.intValue() == code.intValue();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("'code':").append(code).append(",");
        builder.append("'msg':").append(msg).append(",");
        builder.append("'success':").append(success).append(",");
        builder.append("}");
        return builder.toString();
    }
}
