package cn.rh.flash.bean.entity.dzpower;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import java.util.Date;

@Entity(name = "t_dzgoods_powerbanktask")
@Table(appliesTo = "t_dzgoods_powerbanktask", comment = "充电宝返佣任务")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PowerBankTask extends BaseEntity{

    //
    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '编号'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;

    @Column(name = "pbidw", columnDefinition = "VARCHAR(32) COMMENT '产品编号'")
    private String pbidw;
    @Column(name = "image", columnDefinition = "VARCHAR(500) COMMENT '图片'")
    private String image;
    @Column(name = "name", columnDefinition = "VARCHAR(500) COMMENT '名称'")
    private String name;
    @Column(name = "banner_type", columnDefinition = "VARCHAR(30) COMMENT '产品类型'")
    private String bannerType;
    @Column(name = "pay_price", columnDefinition = "decimal(30,6) COMMENT '单天返金额'")  // 单天返金额
    private Double payPrice;
    @Column(name = "tota_quantity", columnDefinition = "int COMMENT '购买数量'")
    private Integer totalQuantity;

    @Column(name = "last_time", columnDefinition = "DATETIME COMMENT '最后一次返佣时间'")
    private Date lastTime;

    @Column(name = "expire_time", columnDefinition = "DATETIME COMMENT '到期时间'")
    private Date expireTime;

    @Column(name = "is_refund", columnDefinition = "int COMMENT '到期是否已经退款 空或1为未退 2为已退'")
    private Integer isRefund;

    @Column(name = "remark", columnDefinition = "text COMMENT '备注'")
    private String remark;

    @Column(name = "start_time", columnDefinition = "DATETIME COMMENT '开始时间'")
    private Date startTime;

    @Column(name = "end_time", columnDefinition = "DATETIME COMMENT '结束时间'")
    private Date endTime;

    @Column(name = "hours", columnDefinition = "VARCHAR(30) COMMENT '执行档次(几小时)'")
    private String hours;

    @Column(name = "vip_type", columnDefinition = "VARCHAR(30) COMMENT 'ViP类型'")
    private String vipType;

}
