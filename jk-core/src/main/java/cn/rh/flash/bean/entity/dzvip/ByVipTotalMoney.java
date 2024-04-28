package cn.rh.flash.bean.entity.dzvip;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzvip_byviptotalmoney")
@Table(appliesTo = "t_dzvip_byviptotalmoney", comment = "用户购买VIP累计金额",indexes = {@Index(name = "index_uid",columnNames = "uid")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class ByVipTotalMoney extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "total_money", columnDefinition = "decimal(30,6) COMMENT '累计购买vip金额'")
    private Double totalMoney;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
}
