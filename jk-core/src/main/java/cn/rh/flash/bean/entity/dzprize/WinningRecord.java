package cn.rh.flash.bean.entity.dzprize;


import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.bean.entity.system.User;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity(name = "t_dzprize_winningrecord")
@Table(appliesTo = "t_dzprize_winningrecord", comment = "中奖记录表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class WinningRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(32) COMMENT '账号'")
    private String account;

    @Column(name = "prize_idw", columnDefinition = "VARCHAR(32) COMMENT '奖品ID'")
    private String prizeIdw;

    @Column(name = "amount", columnDefinition = "decimal(30,6) COMMENT '中奖金额'")
    private Double amount;

    @Column(name = "prize_type", columnDefinition = "VARCHAR(32) COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动 8、夺宝活动'")
    private String prizeType;

    @Column(name = "prize_name", columnDefinition = "VARCHAR(32) COMMENT '奖品名称'")
    private String prizeName;

    @Column(name = "surplus_number", columnDefinition = "int COMMENT '剩余抽奖次数'")
    private Integer surplusNumber;

    @Column(name = "hunt_record_idw", columnDefinition = "VARCHAR(32) COMMENT '用户参与夺宝ID'")//夺宝活动专用
    private String huntRecordIdw;

    @JoinColumn(name = "source_invitation_code", referencedColumnName="ucode", insertable = false, updatable = false, foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne
    private User user;  //顶级账号

}
