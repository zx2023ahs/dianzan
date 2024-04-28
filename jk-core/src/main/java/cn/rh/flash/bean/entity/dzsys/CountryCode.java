package cn.rh.flash.bean.entity.dzsys;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzsys_country")
@Table(appliesTo = "t_dzsys_country", comment = "国家区号")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CountryCode  extends BaseEntity {

    // 国家区号：国家名称、logo、国家码、国家代号、排序号
    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "country_name", columnDefinition = "VARCHAR(50) COMMENT '国家名称'")
    private String countryName;
    @Column(name = "country_name_english", columnDefinition = "VARCHAR(200) COMMENT '国家英文名称'")
    private String countryNameEnglish;
    @Column(name = "logo", columnDefinition = "VARCHAR(512) COMMENT 'logo'")
    private String logo;
    @Column(name = "country_code", columnDefinition = "VARCHAR(50) COMMENT '国家码'")
    private String countryCode;
    @Column(name = "country_code_number", columnDefinition = "VARCHAR(50) COMMENT '国家代号'")
    private String countryCodeNumber;
    @Column(name = "queue_number", columnDefinition = "int COMMENT '排序号'")
    private Integer queueNumber;

}
