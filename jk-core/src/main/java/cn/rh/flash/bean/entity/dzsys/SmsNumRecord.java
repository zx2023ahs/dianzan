package cn.rh.flash.bean.entity.dzsys;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzsys_smsnumrecord")
@Table(appliesTo = "t_dzsys_smsnumrecord", comment = "日发送短信次数记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class SmsNumRecord extends BaseEntity {

    @Column(name = "country_code_number", columnDefinition = "VARCHAR(50) COMMENT '国家代号'")
    private String countryCodeNumber;

    @Column(name = "phone", columnDefinition = "VARCHAR(30) COMMENT '手机号'")
    private String phone;

    @Column(name = "count", columnDefinition = "INT COMMENT '次数'")
    private Integer count;

    @Column(name = "day", columnDefinition = "VARCHAR(30) COMMENT '日期'")
    private String day;
}
