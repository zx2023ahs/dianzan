package cn.rh.flash.bean.entity.dzprize;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzprize_huntingrecord")
@Table(appliesTo = "t_dzprize_huntingrecord", comment = "夺宝参与记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class HuntingRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'",nullable = false)
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'",nullable = false)
    private String sourceInvitationCode;

    @Column(name = "account", columnDefinition = "VARCHAR(32) COMMENT '账号'",nullable = false)
    private String account;

    @Column(name = "hunt_idw", columnDefinition = "VARCHAR(32) COMMENT '夺宝奖品idw'",nullable = false)//夺宝奖品idw
    private String huntIdw;

    @Column(name = "is_fabricate", columnDefinition = "CHAR(2) DEFAULT '0' COMMENT '是否造假数据 0真 1假'",nullable = false)
    private String isFabricate;

    @Column(name = "prize_name", columnDefinition = "VARCHAR(32) COMMENT '奖品名称'")
    private String prizeName;

}
