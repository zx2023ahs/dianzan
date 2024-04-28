package cn.rh.flash.bean.entity.dzcredit;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzcredit_creditconfig")
@Table(appliesTo = "t_dzcredit_creditconfig", comment = "信誉分配置")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CreditConfig extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "credit_min", columnDefinition = "int COMMENT '信誉分低值'")
    private Integer creditMin;

    @Column(name = "credit_max", columnDefinition = "int COMMENT '信誉分高值'")
    private Integer creditMax;

    @Column(name = "yield", columnDefinition = "decimal(30,6) COMMENT '收益率'")
    private Double yield;
}
