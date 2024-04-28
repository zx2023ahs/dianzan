package cn.rh.flash.bean.entity.dzprize;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzprize_monopoly_record")
@Table(appliesTo = "t_dzprize_monopoly_record", comment = "大富翁用户操作记录表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class MonopolyRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(32) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(32) COMMENT '账号'")
    private String account;

    @Column(name = "prize_id", columnDefinition = "bigint COMMENT '奖品id'")
    private Long prizeId;

    @Column(name = "prize_idw", columnDefinition = "VARCHAR(32) COMMENT '奖品idw'")
    private String prizeIdw;

    @Column(name = "prize_name", columnDefinition = "VARCHAR(32) COMMENT '奖品名称'")
    private String prizeName;

    @Column(name = "prize_pic_url", columnDefinition = "VARCHAR(32) COMMENT '奖品图片'")
    private String prizePicUrl;

    @Column(name = "prize_type", columnDefinition = "VARCHAR(32) COMMENT '奖品类型'")
    private String prizeType;

    @Column(name = "last_prize_type", columnDefinition = "VARCHAR(32) COMMENT '当前操作，7前进/8后退'")
    private String lastPrizeType;

    @Column(name = "dice_points", columnDefinition = "int COMMENT '骰子点数/前进/后退步数'")
    private Integer dicePoints;

    @Column(name = "surplus_number", columnDefinition = "int COMMENT '剩余抽奖次数'")
    private Integer surplusNumber;

    @Column(name = "now_position", columnDefinition = "int COMMENT '用户当前位置'")
    private Integer nowPosition;

    @Column(name = "last_position", columnDefinition = "int COMMENT '用户操作前位置'")
    private Integer lastPosition;

}
