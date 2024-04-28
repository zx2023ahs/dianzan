package cn.rh.flash.service.dzuser;



public class TotalBonusIncomeServiceSql {

    /**
     * @return
     */
    public static String sqlBranchTotal() {
        return String.format(
               " select sum(total_bonus_income) as branchTotal from t_dzuser_totalbonus " +
               " where uid not in (select id from t_dzuser_user where user_type = 2) "
        );
    }
    /*

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "dzversion", columnDefinition = "int COMMENT 'version'")
    private Integer dzversion;

    //用户id、用户账号、赠送彩金 、来源邀请码、version
    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "total_bonus_income", columnDefinition = "decimal(30,6) COMMENT '赠送彩金总收入'")
    private Double totalBonusIncome;
     */
}

