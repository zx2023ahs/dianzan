package cn.rh.flash.bean.entity.appv;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity(name = "t_app_appv")
@Table(appliesTo = "t_app_appv", comment = "版本更新")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Appv extends BaseEntity{

    // 版本号  app下载链接  创建时间  软件类型
    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '编号'")
    private String idw;
    @NotBlank( message = "请输入升级链接" )
    @Column(name = "app_url", columnDefinition = "VARCHAR(500) COMMENT '升级链接'")
    private String appUrl;
    @NotBlank( message = "请输入当前版本号" )
    @Column(name = "version_number", columnDefinition = "VARCHAR(200) COMMENT '版本号'")
    private String versionNumber;

    @NotNull( message = "请输入最低版本号" )
    @Column(name = "min_version_number", columnDefinition = "VARCHAR(200) COMMENT '低于版本限制使用'")
    private String minVersionNumber;

    @Column(name = "app_type", columnDefinition = "VARCHAR(30) COMMENT '软件类型'")
    private String appType;  //    IOS  /  ANDROID

    @Column(name = "dzstatus", columnDefinition = "int COMMENT '状态'")
    private Integer dzstatus;  //  状态  1:启用,2:禁用

}
