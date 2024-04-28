package cn.rh.flash.bean.entity.dzpool;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzpool_resort_messag")
@Table(appliesTo = "t_dzpool_resort_messag", comment = "用户求助记录表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PoolResortMessag extends BaseEntity {

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

    @Column(name = "user_pool_amount", columnDefinition = "decimal(30,6) COMMENT '求助金额'")
    private Double userPoolAmount;

    @Column(name = "content", columnDefinition = "text COMMENT '求助内容'")
    private String content;

    @Column(name = "state", columnDefinition = "TINYINT COMMENT '求助状态(0申请中1失败2成功)'")
    private Integer state;

    @Column(name = "received_amount", columnDefinition = "decimal(30,6) COMMENT '实际到账金额'")
    private Double receivedAmount;

}
