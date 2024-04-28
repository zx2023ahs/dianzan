package cn.rh.flash.bean.entity.dzprize;


import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.config.chinesePattern.ChinesePattern;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Transient;
import java.util.Date;

@Entity(name = "t_dzprize_luckydraw")
@Table(appliesTo = "t_dzprize_luckydraw", comment = "抽奖活动表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class LuckyDraw extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "name", columnDefinition = "VARCHAR(32) COMMENT '抽奖活动名称'")
    @ChinesePattern(regexp = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D]+",message = "禁止输入中文",groups = {ChinesePattern.OnUpdate.class})
    private String name;

    @Column(name = "prize_type", columnDefinition = "VARCHAR(32) COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动'")
    private String prizeType;

    @Column(name = "status", columnDefinition = "VARCHAR(32) COMMENT '活动状态'")
    private String status; // 1:开启,2:禁用,3:关闭

    @Column(name = "start_time", columnDefinition = "DATETIME COMMENT '活动开始时间'")
    private Date startTime;

    @Column(name = "end_time", columnDefinition = "DATETIME COMMENT '活动结束时间'")
    private Date endTime;

    @Column(name = "remark", columnDefinition = "text COMMENT '活动说明'")
    private String remark;

    @Transient
    private boolean isExpire;

}

