package cn.rh.flash.bean.entity.dzscore;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzscore_scoreprize")
@Table(appliesTo = "t_dzscore_scoreprize", comment = "积分奖品")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ScorePrize extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "score", columnDefinition = "decimal(30,6) COMMENT '消耗积分'")
    private Double score;

    @Column(name = "url", columnDefinition = "VARCHAR(200) COMMENT '奖品图片'")
    private String url;

    @Column(name = "types", columnDefinition = "VARCHAR(32) COMMENT '奖品类型   1 余额  2 实物'")
    private String types;

    @Column(name = "prize_name", columnDefinition = "VARCHAR(32) COMMENT '奖品名称'")
    private String prizeName;

    @Column(name = "max_vip", columnDefinition = "int COMMENT '最大VIP等级'")
    private Integer maxVip;

    @Column(name = "sort", columnDefinition = "int COMMENT '排序'")
    private Integer sort;

    @Column(name = "amount", columnDefinition = "decimal(30,6) COMMENT '虚拟货币金额 实物为0'")
    private Double amount;

    @Column(name = "prize_type", columnDefinition = "VARCHAR(32) COMMENT '活动类型  1、签到 8、夺宝活动'")//不同活动有不同的积分，不通用
    private String prizeType;

}
