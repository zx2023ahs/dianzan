package cn.rh.flash.bean.vo.dzuser;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StraightListVo {



    @NotNull( message = "请输入账号")
    private String accounts;

    @NotNull( message = "请输入金额")
    private Double money;

}
