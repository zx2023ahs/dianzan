package cn.rh.flash.bean.vo.dz;

import lombok.Data;

import java.util.Date;


@Data
public class LevelUserVo {

    private String countryCodeNumber; // 国家码

    private String account; // 手机号

    private Double userBalance; // 余额

    private String vipTypeStr; // vip类型

    private Long withNum; // 提现次数

    private Date registrationTime; // 注册时间
}
