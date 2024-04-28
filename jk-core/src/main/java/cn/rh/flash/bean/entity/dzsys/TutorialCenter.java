package cn.rh.flash.bean.entity.dzsys;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzsys_tutorialcenter")
@Table(appliesTo = "t_dzsys_tutorialcenter", comment = "教程中心")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TutorialCenter extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "title", columnDefinition = "VARCHAR(32) COMMENT '标题'")
    private String title;

    @Column(name = "type", columnDefinition = "VARCHAR(32) COMMENT '教程类型'") // 1 文本 2 视频
    private String type;

    @Column(name = "status", columnDefinition = "VARCHAR(32) COMMENT '状态'") // 1:启用,2:禁用
    private String status;

    @Column(name = "video_url", columnDefinition = "VARCHAR(1000) COMMENT '视频Url'")
    private String videoUrl;

    @Column(name = "text_content", columnDefinition = "TEXT COMMENT '文本内容'")
    private String textContent;
}
