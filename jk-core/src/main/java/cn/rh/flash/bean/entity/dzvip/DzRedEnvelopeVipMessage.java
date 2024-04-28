package cn.rh.flash.bean.entity.dzvip;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity(name = "t_dzvip_red_envelope_message")
@Table(appliesTo = "t_dzvip_red_envelope_message", comment = "Vip红包信息")
@Data
@EntityListeners(AuditingEntityListener.class)
public class DzRedEnvelopeVipMessage extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "vip_type", columnDefinition = "text COMMENT 'ViP类型(多选)'")
    private String vipType;

    @Column(name = "red_envelope_total", columnDefinition = "int COMMENT '红包领取次数'")
    private Integer redEnvelopeTotal;

    @Column(name = "red_envelope_money_max", columnDefinition = "decimal(30,6) COMMENT '红包领取金额最大值'")
    private Double redEnvelopeMoneyMax;

    @Column(name = "red_envelope_money_min", columnDefinition = "decimal(30,6) COMMENT '红包领取金额最小值'")
    private Double redEnvelopeMoneyMin;

}
