package cn.rh.flash.bean.vo.dz;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IpRanking {

    private String ip;  //ip
    private BigDecimal number;  //数量
    private String lastIpCity;  // 城市信息

}
