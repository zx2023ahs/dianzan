package cn.rh.flash.bean.entity.dzuser;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzuser_falsetotal")
@Table(appliesTo = "t_dzuser_falsetotal", comment = "造假统计")
@Data
@EntityListeners(AuditingEntityListener.class)
public class FalseTotal extends BaseEntity {


    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;

    @Column(name = "profit_of_the_day", columnDefinition = "VARCHAR(30) COMMENT '当日收益'")
    private String profitOfTheDay;

    @Column(name = "total_revenue", columnDefinition = "VARCHAR(30) COMMENT '总收入'")
    private String totalRevenue;

    @Column(name = "total_withdrawal_amount", columnDefinition = "VARCHAR(30) COMMENT '可用金额'")
    private String totalWithdrawalAmount;

    @Column(name = "team_size", columnDefinition = "VARCHAR(30) COMMENT '团队规模'")
    private String teamSize;

    @Column(name = "team_report", columnDefinition = "VARCHAR(30) COMMENT '团队报告'")
    private String teamReport;

    @Column(name = "balance", columnDefinition = "VARCHAR(30) COMMENT '收入明细'")
    private String balance;
}
