package cn.rh.flash.bean.dto;


import lombok.Data;

@Data
public class PrizeNumDto {

    private String account; // 用户账号

    private String isAdd;  // 1+2-

    private Integer prizeNum; // 次数

    private String prizeType; // 1 转盘活动 2 投注活动 3 盲盒活动
}
