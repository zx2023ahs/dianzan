package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("大富翁当前用户数据")
public class MonopolyUserAndNumVo {

    private Long id;

    private Long uid;

    private String account;

    private String activityType;

    private String idw;

    private Integer position;

    private String sourceInvitationCode;

    private Integer num;

    private Long prizeNumId;
}
