package cn.rh.flash.bean.entity.dzuser;


import cn.rh.flash.bean.dto.FalseDataForm;
import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.utils.IdWorker;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzuser_falsedata")
@Table(appliesTo = "t_dzuser_falsedata", comment = "造假记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class FalseData extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "false_date", columnDefinition = "TEXT COMMENT '造假数据'")
    private String falseDate;

    @Column(name = "false_type", columnDefinition = "VARCHAR(32) COMMENT '造假方式 1.提现记录 2.交易记录(CDB返佣) 3.用户下级 4.提现记录'") // 1.提现记录 2.交易记录(CDB返佣) 3.用户下级 4.提现记录
    private String falseType;

    @Column(name = "is_del", columnDefinition = "VARCHAR(32) COMMENT '是否删除'") // 1 是 0 否
    private String isDel;

    @Column(name = "remark", columnDefinition = "VARCHAR(200) COMMENT '备注'")
    private String remark;

    public FalseData() {
    }

    public FalseData(FalseDataForm falseDataForm) {
        this.idw = new IdWorker().nextId()+"";
        this.falseDate = falseDataForm.getFalseDate().replace("\n","");
        this.falseType = falseDataForm.getFalseType();
        this.isDel = "0";
    }
}
