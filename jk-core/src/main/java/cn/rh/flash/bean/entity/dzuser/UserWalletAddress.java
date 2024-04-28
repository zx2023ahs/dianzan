package cn.rh.flash.bean.entity.dzuser;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzuser_wallet_address")
@Table(appliesTo = "t_dzuser_wallet_address", comment = "用户钱包地址",indexes = {
        @Index(name = "index_sourcecode",columnNames = "source_invitation_code")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserWalletAddress extends BaseEntity {

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;
    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "wallet_address", columnDefinition = "varchar(50) COMMENT '钱包地址'")
    private String walletAddress;
    @Column(name = "platform_name", columnDefinition = "varchar(50) COMMENT '平台名称'")
    private String platformName;
    @Column(name = "wallet_name", columnDefinition = "varchar(20) COMMENT '姓名'")
    private String walletName;
    @Column(name = "channel_type", columnDefinition = "varchar(20) COMMENT '通道类型'")
    private String channelType;

}
