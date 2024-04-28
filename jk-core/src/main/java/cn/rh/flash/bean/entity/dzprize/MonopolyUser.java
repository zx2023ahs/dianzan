package cn.rh.flash.bean.entity.dzprize;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzprize_monopoly_user")
@Table(appliesTo = "t_dzprize_monopoly_user", comment = "大富翁用户当前位置记录表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class MonopolyUser extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(32) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(32) COMMENT '账号'")
    private String account;

    @Column(name = "activity_type", columnDefinition = "VARCHAR(32) COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动 8、夺宝活动'")
    private String activityType;

    @Column(name = "position", columnDefinition = "VARCHAR(32) COMMENT '当前定位'")
    private Integer position;

}
