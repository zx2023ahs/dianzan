package cn.rh.flash.bean.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
* @Description: 造假数据接收form
* @Author: Skj(老子真TM帅)
* @Date: 2023/4/7
*/
@Data
public class FalseDataForm {

    // 造假方式 1.提现记录 2.交易记录(CDB返佣) 3.用户下级 4.充值记录
    @NotBlank(message = "造假方式不能为空")
    private String falseType;

    // 接收的假数据
    @NotBlank(message = "请求数据不能为空")
    private String falseDate; // 格式  7891234001---1.59---2023-04-02 08:50:56,7891234002---1.59---2023-04-02 08:50:56

}
