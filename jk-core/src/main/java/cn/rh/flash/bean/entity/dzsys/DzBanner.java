package cn.rh.flash.bean.entity.dzsys;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Entity(name = "t_dzsys_dzbanner")
@Table(appliesTo = "t_dzsys_dzbanner", comment = "轮播图")
@Data
@EntityListeners(AuditingEntityListener.class)
public class DzBanner extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    // 图片、类型、跳转链接
    @NotBlank( message = "请上传图片" )
    @Column(name = "image", columnDefinition = "VARCHAR(500) COMMENT '图片'")
    private String image;
    @NotNull( message = "请选择轮播图" )
    @Column(name = "banner_type", columnDefinition = "VARCHAR(30) COMMENT '轮播图类型'")
    private String bannerType;
    @Column(name = "jump_link", columnDefinition = "VARCHAR(500) COMMENT '跳转链接'")
    private String jumpLink;


}
