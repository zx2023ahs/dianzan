package cn.rh.flash.bean.entity.dzsys;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzsys_onlineserve")
@Table(appliesTo = "t_dzsys_onlineserve", comment = "在线客服")
@Data
@EntityListeners(AuditingEntityListener.class)
public class OnlineServe extends BaseEntity {
    // 客服链接、名称、客服类型
    @Column(name = "customer_service_link", columnDefinition = "VARCHAR(500) COMMENT '客服链接'")
    private String customerServiceLink;
    @Column(name = "name", columnDefinition = "VARCHAR(50) COMMENT '名称'")
    private String name;
    @Column(name = "onlines_type", columnDefinition = "VARCHAR(20) COMMENT '客服类型'")
    private String onlinesType;
    @Column(name = "onlines_flag", columnDefinition = "VARCHAR(20) COMMENT '客服状态'")
    private Integer onlinesFlag;  //  1 启用    2 禁用
    @Column(name = "logo", columnDefinition = "VARCHAR(500) COMMENT '客服logo'")
    private String logo;

}
