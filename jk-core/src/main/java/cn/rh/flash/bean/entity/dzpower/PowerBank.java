package cn.rh.flash.bean.entity.dzpower;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity(name = "t_dzgoods_powerbank")
@Table(appliesTo = "t_dzgoods_powerbank", comment = "充电宝")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PowerBank extends BaseEntity{

    // 图片  名称  单天返金额   总数  在线数量  使用中的数量  状态  类型
    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '编号'")
    private String idw;
    @NotBlank( message = "请上传图片" )
    @Column(name = "image", columnDefinition = "VARCHAR(500) COMMENT '图片'")
    private String image;
    @NotBlank( message = "请输入名称" )
    @Column(name = "name", columnDefinition = "VARCHAR(500) COMMENT '名称'")
    private String name;
    @NotNull( message = "请选择产品类型" )
    @Column(name = "banner_type", columnDefinition = "VARCHAR(30) COMMENT '产品类型'")
    private String bannerType;

    @Column(name = "price", columnDefinition = "decimal(30,6) COMMENT '单天返金额'")
    private Double price;

    @Column(name = "tota_quantity", columnDefinition = "int COMMENT '总数量'")
    private Integer totalQuantity = 0;
    @Column(name = "online_quantity", columnDefinition = "int COMMENT '在线数量'")
    private Integer onlineQuantity = 0;
    @Column(name = "quantity_in_use", columnDefinition = "int COMMENT '使用中的数量'")
    private Integer quantityInUse = 0;





    @Column(name = "dzstatus", columnDefinition = "int COMMENT '状态'")
    private Integer dzstatus;

}
