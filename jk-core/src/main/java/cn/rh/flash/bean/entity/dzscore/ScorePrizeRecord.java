package cn.rh.flash.bean.entity.dzscore;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzscore_scoreprizerecord")
@Table(appliesTo = "t_dzscore_scoreprizerecord", comment = "积分奖品记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ScorePrizeRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(32) COMMENT '账号'")
    private String account;

    @Column(name = "prize_idw", columnDefinition = "VARCHAR(32) COMMENT '奖品ID'")
    private String prizeIdw;

    @Column(name = "amount", columnDefinition = "decimal(30,6) COMMENT '中奖金额'")
    private Double amount;

    @Column(name = "score", columnDefinition = "decimal(30,6) COMMENT '消耗积分'")
    private Double score;

    @Column(name = "url", columnDefinition = "VARCHAR(200) COMMENT '奖品图片'")
    private String url;

    @Column(name = "types", columnDefinition = "VARCHAR(32) COMMENT '奖品类型   1 余额  2 实物'")
    private String types;

    @Column(name = "prize_name", columnDefinition = "VARCHAR(32) COMMENT '奖品名称'")
    private String prizeName;

    @Column(name = "surplus_score", columnDefinition = "decimal(30,6) COMMENT '剩余积分'")
    private Double surplusScore;

    @Column(name = "prize_type", columnDefinition = "VARCHAR(32) DEFAULT 6 COMMENT '活动类型  1、签到 8、夺宝活动'")//不同活动有不同的积分，不通用
    private String prizeType;

}
