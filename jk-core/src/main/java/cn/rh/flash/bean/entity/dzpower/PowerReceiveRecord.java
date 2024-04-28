package cn.rh.flash.bean.entity.dzpower;


import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.bean.entity.system.User;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "t_dzgoods_rowerreceiverecord")
@Table(appliesTo = "t_dzgoods_rowerreceiverecord", comment = "充电宝收益手动领取记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PowerReceiveRecord  extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;

    @Column(name = "taskidw", columnDefinition = "VARCHAR(32) COMMENT '充电宝任务编号'")
    private String taskidw;

    @Column(name = "pbidw", columnDefinition = "VARCHAR(32) COMMENT '充电宝编号'")
    private String pbidw;

    @Column(name = "image", columnDefinition = "VARCHAR(500) COMMENT '图片'")
    private String image;

    @Column(name = "name", columnDefinition = "VARCHAR(500) COMMENT '名称'")
    private String name;

    @Column(name = "pay_price", columnDefinition = "decimal(30,6) COMMENT '小时价'")  // 单天返佣收益÷档次小时
    private Double payPrice;

    @Column(name = "income_hour", columnDefinition = "bigint COMMENT '投放小时'")
    private Long incomeHour;

    @Column(name = "tota_quantity", columnDefinition = "int COMMENT '购买数量'")
    private Integer totalQuantity;

    @Column(name = "status", columnDefinition = "int COMMENT '领取状态'") // 1 未领取 2 已领取
    private Integer status;

    @Column(name = "money", columnDefinition = "decimal(30,6) COMMENT '预计收益'")
    private Double money;

    @Column(name = "start_time", columnDefinition = "DATETIME COMMENT '开始时间'")
    private Date startTime;

    @Column(name = "end_time", columnDefinition = "DATETIME COMMENT '结束时间'")
    private Date endTime;

    @Column(name = "vip_type", columnDefinition = "VARCHAR(30) COMMENT 'ViP类型'")
    private String vipType;


    @Column(name = "credit", columnDefinition = "int COMMENT '当前信誉分'")
    private Integer credit;

    @Column(name = "yield", columnDefinition = "decimal(30,6) COMMENT '收益率'")
    private Double yield;

    @JoinColumn(name = "source_invitation_code", referencedColumnName="ucode", insertable = false, updatable = false, foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne
    private User user;  //顶级账号
}
