package cn.rh.flash.bean.entity.dzuser;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzuser_balance")
@Table(appliesTo = "t_dzuser_balance", comment = "用户余额",indexes = {@Index(name = "index_sourcecode",columnNames = "source_invitation_code")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserBalance extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    // 用户id、用户账号、用户余额、钱包地址、来源邀请码、version
    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "user_balance", columnDefinition = "decimal(30,6) COMMENT '用户余额'")
    private Double userBalance;
    @Column(name = "wallet_address", columnDefinition = "VARCHAR(50) COMMENT '钱包地址'") // 提款地址
    private String walletAddress;
    @Column(name = "dzversion", columnDefinition = "int COMMENT 'version'")
    private Integer dzversion;
    @Column(name = "channel_type", columnDefinition = "VARCHAR(50) COMMENT '通道类型'")//通道类型     USDT.Polygon                USDT.TRC20
    private String channelType;

}
