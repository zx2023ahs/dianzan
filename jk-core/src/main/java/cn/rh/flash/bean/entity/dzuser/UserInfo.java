package cn.rh.flash.bean.entity.dzuser;

import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.config.chinesePattern.ChinesePattern;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "t_dzuser_user")
@Table(appliesTo = "t_dzuser_user", comment = "用户信息")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserInfo extends BaseEntity {

    // 1禁止 2 ok
    @Column(name = "limit_buy_cdb", columnDefinition = "int COMMENT '限制购买CDB'")
    private Integer limitBuyCdb;
    @Column(name = "limit_drawing", columnDefinition = "int COMMENT '限制提款'")
    private Integer limitDrawing;
    @Column(name = "limit_profit", columnDefinition = "int COMMENT '限制收益'")
    private Integer limitProfit;
    @Column(name = "limit_code", columnDefinition = "int COMMENT '限制邀请码'")
    private Integer limitCode;

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'",updatable = false )
    private String sourceInvitationCode;

    // 国家代号、上级邀请码、邀请码、来源邀请码、账号、密码、支付密码、验证器密码、用户类型、ViP类型、ViP到期时间、注册ip、注册时间、状态

    @NotBlank(message = "请选择对应的国家")
    @Column(name = "country_code_number", columnDefinition = "VARCHAR(50) COMMENT '国家代号'")
    private String countryCodeNumber;

    @NotBlank(message = "请输入上级邀请码")
    @Column(name = "superior_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '上级邀请码'")
    private String superiorInvitationCode;

    @Column(name = "invitation_code", columnDefinition = "VARCHAR(20) COMMENT '邀请码'")
    private String invitationCode;
    @Column(name = "name", columnDefinition = "VARCHAR(30) COMMENT '用户名'")
    private String name;
//    @NotBlank(message = "请输入真实姓名")
    @Column(name = "real_name", columnDefinition = "VARCHAR(30) COMMENT '真实姓名'")
    private String realName;
    @Column(name = "head_portrait_key", columnDefinition = "VARCHAR(500) COMMENT '头像'")
    private String headPortraitKey;


    @Column(name = "pinvitation_code", columnDefinition = "text COMMENT '父级递归'")
    private String pinvitationCode;
    @Column(name = "levels", columnDefinition = "int COMMENT '级别'")
    private Integer levels;

    @NotBlank(message = "请输入电话号码")
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @NotBlank(message = "请输入密码")
    @Column(name = "password", columnDefinition = "VARCHAR(50) COMMENT '密码'")
    private String password;
    //@NotBlank(message = "请输入支付密码")
    @Column(name = "payment_password", columnDefinition = "VARCHAR(50) COMMENT '支付密码'")
    private String paymentPassword;

    @Column(name = "authenticator_password", columnDefinition = "VARCHAR(20) COMMENT '验证器密码'")
    private String authenticatorPassword;

    @NotBlank(message = "请选择用户类型")
    @Column(name = "user_type", columnDefinition = "VARCHAR(20) COMMENT '用户类型'")
    private String userType;
    //@NotBlank(message = "请选择ViP类型")
    @Column(name = "vip_type", columnDefinition = "VARCHAR(20) COMMENT 'ViP类型'")
    private String vipType;
    @Column(name = "vip_expire_date", columnDefinition = "DATETIME COMMENT 'ViP到期时间'")
    private Date vipExpireDate;
    @Column(name = "register_ip", columnDefinition = "VARCHAR(100) COMMENT '注册ip'")
    private String registerIp;
    @Column(name = "last_ip", columnDefinition = "VARCHAR(100) COMMENT '最后一次登录ip'")
    private String lastIp;

    @Column(name = "register_ip_city", columnDefinition = "text COMMENT '注册ip区域'")
    private String registerIpCity;
    @Column(name = "last_ip_city", columnDefinition = "text COMMENT '最后一次登录ip区域'")
    private String lastIpCity;


    @Column(name = "last_time", columnDefinition = "DATETIME COMMENT '最后一次登录时间'")
    private Date lastTime;
    @Column(name = "registration_time", columnDefinition = "DATETIME COMMENT '注册时间'")
    private Date registrationTime;

    @NotNull(message = "请选择用户状态")
    @Column(name = "dzstatus", columnDefinition = "int COMMENT '状态'")
    private Integer dzstatus;  //   1启用    2停用   3 已删除

    @Column(name = "remark", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    @ChinesePattern(regexp = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D]+",message = "禁止输入中文",groups = {ChinesePattern.OnUpdate.class})
    private String remark;

    @Column(name = "fidw", columnDefinition = "VARCHAR(32) COMMENT '造假数据唯一值'")
    private String fidw;

    // PC段需要显示的字段↓↓↓↓↓↓ start ↓↓↓↓↓↓
    @Transient
    private Double totalRechargeAmountLeft; // 充值总金额

    @Transient
    private Double teamVIPOpeningTotalRebate; // 开通VIP金额

    @Transient
    private Double sourceUserAccount; // 充电宝返佣总金额

    @Transient
    private Double remakeNumOK; // 已成功提现的金额

    @Transient
    private Double moneyOK; // 已成功提现的金额

    @Transient
    private Double moneyNO; // 正在审核中的金额

    @Transient
    private Double totalBonusIncomeLeft; // 赠送彩金金额

    @Transient
    private Double userBalanceLeft; // 用户余额

    @Transient
    private Double teamVIPOpeningTotal; // 团队开通VIP总返佣

    // ↑↑↑↑↑↑ end ↑↑↑↑↑↑


    @Transient
    private String vipImg; // VIP图片

    @Transient
    private String walletAddress; // 钱包地址

    @Transient
    private String superAccount; // 顶级账号


    @Transient
    private Integer numberOfSubordinates; //下级数量

    @Transient
    private BigDecimal profitAmount;  //盈利金额


//    @JoinColumn(name = "vip_type", referencedColumnName="vip_type", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
//    @ManyToOne
//    private DzVipMessage dzVipMessage; // Vip信息

//    // 直冲直扣  TransactionRecord（交易记录） CompensationRecord（彩金记录）  TotalBonusIncome（彩金总收入）
//    @JoinColumn(name = "id", referencedColumnName="uid", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
//    @ManyToOne
//    private TotalBonusIncome totalBonusIncome; // 赠送彩金总收入

//    @JoinColumn(name = "id", referencedColumnName="uid", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
//    @ManyToOne
//    private TotalRechargeAmount totalRechargeAmount; // 充值总金额

//    @JoinColumn(name = "id", referencedColumnName="uid", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
//    @ManyToOne
//    private TotalWithdrawalAmount totalWithdrawalAmount; // 提现总金额
//
//    @JoinColumn(name = "id", referencedColumnName="uid", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
//    @ManyToOne
//    private TeamVIPActivationTotalRevenue teamVIPActivationTotalRevenue; // 团队vip开通总返佣

//    @JoinColumn(name = "id", referencedColumnName="uid", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
//    @ManyToOne
//    private TotalBonusPb totalBonusPb; // 充电宝返佣总金额

    /*

        余额关系

        总收入 =   任务收入 + 团队任务收入 + 团队vip开通返佣 + 赠送彩金（手充）

        我的余额 =  userinfo.get余额  [ 等同于  总收入-共享划转-购买vip-提现+ 充值  ]
     */
//    @JoinColumn(name = "id", referencedColumnName="uid", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
//    @ManyToOne
//    private UserBalance userBalance; // 用户余额



}
