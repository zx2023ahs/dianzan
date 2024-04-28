package cn.rh.flash.bean.dto;


import lombok.Data;

@Data
public class UserCreditDto {

    private String account; // 用户账号

    private String isAdd;  // 1+2-

    private Integer credit; // 分数
}
