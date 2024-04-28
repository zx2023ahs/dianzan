package cn.rh.flash.bean.entity.dzcredit;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzcredit_usercredit")
@Table(appliesTo = "t_dzcredit_usercredit", comment = "用户信誉分")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserCredit extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(32) COMMENT '账号'")
    private String account;

    @Column(name = "credit", columnDefinition = "int COMMENT '信誉分'")
    private Integer credit;

    @Column(name = "final_date", columnDefinition = "VARCHAR(32) COMMENT '设备最后一次运营时间'") // yyyy-MM-dd
    private String finalDate;

    @Column(name = "status", columnDefinition = "VARCHAR(32) COMMENT '信誉分状态'")
    private String status; // 1,正常 2,3天未运行 3,5天未运行   10   15

    @Column(name = "vip_type", columnDefinition = "VARCHAR(32) COMMENT 'vip类型'")
    private String vipType;
}
