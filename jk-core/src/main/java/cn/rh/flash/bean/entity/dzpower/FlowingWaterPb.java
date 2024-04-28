package cn.rh.flash.bean.entity.dzpower;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import java.util.Date;

@Entity(name = "t_dzgoods_flowingwaterpb")
@Table(appliesTo = "t_dzgoods_flowingwaterpb", comment = "充电宝返佣流水")
@Data
@EntityListeners(AuditingEntityListener.class)
public class FlowingWaterPb extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "money", columnDefinition = "decimal(30,6) COMMENT '金额'")
    private Double money;

    @Column(name = "source_user_account", columnDefinition = "VARCHAR(30) COMMENT '来源用户账号'")
    private String sourceUserAccount;

    @Column(name = "relevels", columnDefinition = "int COMMENT '相对层级'")
    private Integer relevels;

    @Column(name = "dzstatus", columnDefinition = "int COMMENT '状态'") // 1 未处理 2 已处理
    private Integer dzstatus;

    @Column(name = "flowing_water_date", columnDefinition = "DATETIME COMMENT '流水时间'")
    private Date flowingWaterDate;

    @Column(name = "task_idw", columnDefinition = "VARCHAR(32) COMMENT '返佣任务编号'")
    private String taskIdw;


}
