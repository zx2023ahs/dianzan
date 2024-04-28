package cn.rh.flash.bean.entity.dzsys;


import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import java.util.Date;


@Entity(name = "t_dzsys_syslog")
@Table(appliesTo = "t_dzsys_syslog", comment = "系统日志")
@Data
@EntityListeners(AuditingEntityListener.class)
public class SysLog extends BaseEntity {

    // 时间 操作人名称  操作ID  APP pc  干了啥(枚举)   备注
    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "operator", columnDefinition = "VARCHAR(32) COMMENT '操作人'")
    private String operator;

    @Column(name = "obj_id", columnDefinition = "bigint COMMENT '操作实体主键ID'")
    private Long objId;

    @Column(name = "operator_system", columnDefinition = "VARCHAR(32) COMMENT '操作端  APP/PC'")
    private String operatorSystem;

    @Column(name = "operation", columnDefinition = "VARCHAR(32) COMMENT '具体操作'")
    private SysLogEnum operation;

    @Column(name = "operation_time", columnDefinition = "DATETIME COMMENT '操作时间'")
    private Date operationTime;

    @Column(name = "remark", columnDefinition = "VARCHAR(500) COMMENT '备注'") // 谁在什么时间干了什么
    private String remark;

}
