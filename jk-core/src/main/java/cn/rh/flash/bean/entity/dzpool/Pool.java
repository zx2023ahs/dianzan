package cn.rh.flash.bean.entity.dzpool;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzpool_pool")
@Table(appliesTo = "t_dzpool_pool", comment = "公积金池表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class  Pool extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "amount", columnDefinition = "decimal(30.6) COMMENT '公积金池'")
    private Double amount;

    @Column(name = "img", columnDefinition = "varchar(200) COMMENT '图片'")
    private String img;

    @Column(name = "version", columnDefinition = "varchar(20) COMMENT '版本'")
    private String version;
}
