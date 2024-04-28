package cn.rh.flash.bean.entity.dzsys;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzsys_userippermissions")
@Table(appliesTo = "t_dzsys_userippermissions", comment = "用户IP权限")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserIpPermissions extends BaseEntity {

    @Column(name = "ip", columnDefinition = "VARCHAR(32) COMMENT 'IP'")
    private String ip;
    @Column(name = "types", columnDefinition = "VARCHAR(32) COMMENT 'PC/MOVE'")
    private String types;
    @Column(name = "black_or_white", columnDefinition = "VARCHAR(32) COMMENT 'BlackList/WhiteList'")
    private String blackOrWhite;

}
