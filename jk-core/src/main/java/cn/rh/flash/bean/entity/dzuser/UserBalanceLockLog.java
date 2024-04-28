package cn.rh.flash.bean.entity.dzuser;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import java.util.Date;

@Entity(name = "t_dzuser_userbalancelocklog")
@Table(appliesTo = "t_dzuser_userbalancelocklog", comment = "用户余额日志锁记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserBalanceLockLog extends BaseEntity {

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;

    @Column(name = "money", columnDefinition = "decimal(30,6) COMMENT '金额'")
    private Double money;

    @Column(name = "dzstatus", columnDefinition = "int COMMENT '状态'") // 1 已处理 2 未处理
    private Integer dzstatus;

    @Column(name = "handle_time", columnDefinition = "DATETIME COMMENT '处理时间'")
    private Date handleTime;
}
