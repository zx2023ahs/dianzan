package cn.rh.flash.bean.entity.dzprize;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Transient;

@Entity(name = "t_dzprize_prize")
@Table(appliesTo = "t_dzprize_prize", comment = "抽奖奖品表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Prize extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "prize_name", columnDefinition = "VARCHAR(32) COMMENT '奖品名称'")
    private String prizeName;

    @Column(name = "prize_nice", columnDefinition = "VARCHAR(32) COMMENT '奖品备注'")
    private String prizeNice;

    @Column(name = "url", columnDefinition = "VARCHAR(200) COMMENT '奖品图片'")
    private String url;

    @Column(name = "types", columnDefinition = "VARCHAR(32) COMMENT '奖品类型  1 余额  2 实物  3积分增加   4积分减少   5抽奖次数增加    6抽奖次数减少    7前进步数   8后退步数'")
    private String types;

    @Column(name = "prize_type", columnDefinition = "VARCHAR(32) COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动 8、夺宝活动  9扭蛋 10大富翁  0下架'")
    private String prizeType;

    @Column(name = "amount", columnDefinition = "decimal(30,6) COMMENT '虚拟货币金额 实物为0   当奖品为'") // 共享单车 为倍率
    private Double amount;

    @Column(name = "winning_chance", columnDefinition = "VARCHAR(32) COMMENT '中奖几率千分比'")
    private String winningChance;

    @Column(name = "total_number", columnDefinition = "int UNSIGNED COMMENT '参加总需人数   也作为大富翁数量字段使用'")//夺宝活动使用
    private Integer totalNumber;

    @Column(name = "participate_number", columnDefinition = "int UNSIGNED COMMENT '已经参加人数   也作为大富翁排序字段使用'")//夺宝活动使用
    private Integer participateNumber;

    @Column(name = "is_end", columnDefinition = "CHAR(2) DEFAULT '0' COMMENT '是否夺宝结束 0正常 1结束'",nullable = false)//夺宝活动使用
    private String isEnd;

    @Transient
    private String account;

}
