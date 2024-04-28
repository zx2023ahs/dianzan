package cn.rh.flash.bean.entity.dzsys;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Transient;

@Entity(name = "t_dzsys_mulutilinguallang")
@Table(appliesTo = "t_dzsys_mulutilinguallang", comment = "多语言")
@Data
@EntityListeners(AuditingEntityListener.class)
public class MultilingualLang extends BaseEntity {

    @Column(name = "lang_key", columnDefinition = "VARCHAR(50) COMMENT '多语言key'")
    private String langKey;

    @Column(name = "remark", columnDefinition = "VARCHAR(200) COMMENT '说明'")
    private String remark;

    @Column(name = "lang_context", columnDefinition = "TEXT COMMENT '多语言内容'")
    private String langContext;

    @Column(name = "lang_code", columnDefinition = "VARCHAR(50) COMMENT '语言'")
    private String langCode;

    @Transient
    private String name;

}
