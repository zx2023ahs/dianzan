package cn.rh.flash.bean.entity.dzpool;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzpool_contribute_messag")
@Table(appliesTo = "t_dzpool_contribute_messag", comment = "用户捐助记录表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PoolContributeMessag extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "pool_idw", columnDefinition = "VARCHAR(32) COMMENT '公积金池唯一值'")
    private String poolIdw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;

    @Column(name = "user_pool_amount", columnDefinition = "decimal(30,6) COMMENT '捐助金额'")
    private Double userPoolAmount;

}
