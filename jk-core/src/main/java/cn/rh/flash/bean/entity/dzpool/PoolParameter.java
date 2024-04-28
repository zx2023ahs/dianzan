package cn.rh.flash.bean.entity.dzpool;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzpool_parameter")
@Table(appliesTo = "t_dzpool_parameter", comment = "爱心等级设定表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PoolParameter extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "maximum", columnDefinition = "decimal(30.6) COMMENT '最大值'")
    private Double maximum;

    @Column(name = "minimum", columnDefinition = "decimal(30.6) COMMENT '最小值'")
    private Double minimum;

    @Column(name = "name", columnDefinition = "varchar(20) COMMENT '等级名称'")
    private String name;

    @Column(name = "logo", columnDefinition = "varchar(200) COMMENT 'logo'")
    private String logo;


}
