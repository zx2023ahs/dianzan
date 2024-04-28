package cn.rh.flash.bean.entity.dzscore;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzscore_signinset")
@Table(appliesTo = "t_dzscore_signinset", comment = "签到配置")
@Data
@EntityListeners(AuditingEntityListener.class)
public class SignInSet extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "day_index", columnDefinition = "int COMMENT '连续签到第几天'")
    private Integer dayIndex;

    @Column(name = "reward_amount", columnDefinition = "decimal(30,6) COMMENT '奖励积分'")
    private Double rewardAmount;

}
