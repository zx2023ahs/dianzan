package cn.rh.flash.bean.entity.dzsys;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;

@Entity(name = "t_dzsys_paymentchannel")
@Table(appliesTo = "t_dzsys_paymentchannel", comment = "通道")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PaymentChannel extends BaseEntity {
    //通道名称、密钥、币种、货币代码、备注
    @NotBlank( message = "请选择通道名称" )//银行名称
    @Column(name = "channel_name", columnDefinition = "VARCHAR(500) COMMENT '通道名称'")
    private String channelName;
    @NotBlank( message = "请输入通道网关" )//银行代码
    @Column(name = "dzkey", columnDefinition = "VARCHAR(200) COMMENT '通道网关'")
    private String dzkey;
    @NotBlank( message = "请选择币种" )//国内--cny
    @Column(name = "currency", columnDefinition = "VARCHAR(50) COMMENT '币种'")
    private String currency;
    @NotBlank( message = "请输入商户号" )//银行卡号
    @Column(name = "currency_code", columnDefinition = "VARCHAR(50) COMMENT '商户号'")
    private String currencyCode;
    @Column(name = "remark", columnDefinition = "VARCHAR(500) COMMENT '备注'")//姓名
    private String remark;
    @Column(name = "private_key", columnDefinition = "VARCHAR(3000) COMMENT '密钥'")
    private String privateKey;

    @Column(name = "is_payment", columnDefinition = "int COMMENT '开启支付(1开启/0关闭)'")
    private Integer isPayment;

    @Column(name = "is_withdrawal", columnDefinition = "int COMMENT '开启提现(1开启/0关闭)'")
    private Integer isWithdrawal;

    @Column(name = "public_key", columnDefinition = "VARCHAR(3000) COMMENT '平台公钥'")
    private String publicKey;

    @Column(name = "model", columnDefinition = "VARCHAR(255) COMMENT '通道编码'")
    private String model;

    @Column(name = "bank_code", columnDefinition = "VARCHAR(255) COMMENT '银行编码-当通道编码是网银支付时 必填'")
    private String bankCode;

    @Column(name = "sort", columnDefinition = "int COMMENT '排序' DEFAULT 0 ",nullable = false)
    private Integer sort;

}
