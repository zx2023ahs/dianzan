package cn.rh.flash.bean.entity.dzsys;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;


@Entity(name = "t_dzsys_officialnews")
@Table(appliesTo = "t_dzsys_officialnews", comment = "公告信息")
@Data
@EntityListeners(AuditingEntityListener.class)
public class DzOfficialNews extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    //标题、广告类型、链接、富文本
    @NotBlank( message = "请输入标题" )
    @Column(name = "title", columnDefinition = "VARCHAR(200) COMMENT '标题'")
    private String title;
    @NotBlank( message = "请选择公告类型" )
    @Column(name = "official_type", columnDefinition = "VARCHAR(30) COMMENT '公告类型'")
    private String officialType;
    @Column(name = "jump_link", columnDefinition = "VARCHAR(500) COMMENT '跳转链接'")
    private String jumpLink;
    @NotBlank( message = "请输入内容" )
    @Column(name = "dzcontent", columnDefinition = "text COMMENT '内容'")
    private String dzcontent;
    @NotBlank( message = "请选择语言" )
    @Column(name = "language", columnDefinition = "VARCHAR(20) COMMENT '语言'")
    private String language;


}
