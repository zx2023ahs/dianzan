package cn.rh.flash.bean.entity.dzsys;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;

@Entity(name = "t_dzsys_sms")
@Table(appliesTo = "t_dzsys_sms", comment = "短信信息")
@Data
@EntityListeners(AuditingEntityListener.class)
public class SmsMessage extends BaseEntity {
    //平台名称（塞邮等）、名称、appid、appkey、appse

    @NotBlank( message = "请选择平台名称" )
    @Column(name = "platform_name", columnDefinition = "VARCHAR(10) COMMENT '平台名称'")
    private String platformName;
    @NotBlank( message = "请输入名称" )
    @Column(name = "name", columnDefinition = "VARCHAR(50) COMMENT '名称'")
    private String name;
    @NotBlank( message = "请输入appid" )
    @Column(name = "appid", columnDefinition = "VARCHAR(200) COMMENT 'appid'")
    private String appid;
    @NotBlank( message = "请输入appkey" )
    @Column(name = "appkey", columnDefinition = "VARCHAR(200) COMMENT 'appkey'")
    private String appkey;
    @NotBlank( message = "请输入appse" )
    @Column(name = "appse", columnDefinition = "VARCHAR(200) COMMENT 'appse'")
    private String appse;

    @Column(name = "dzstatus", columnDefinition = "int COMMENT '状态'")
    private Integer dzstatus;

    @Column(name = "api_url", columnDefinition = "VARCHAR(200) COMMENT 'base_url'")
    private String apiUrl;


}
