package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("团队用户报告结果")
public class UserInfoSubVo {

    private Long id;

    private String invitationCode;

    private String account;

    private Double Commission;

}
