package cn.rh.flash.bean.dto.api;

import lombok.Data;

@Data
public class MonopolyRecordDto {

    private Long prizeId;

    private String prizeIdw;

    private String prizeName;

    private String prizePicUrl;

    private String prizeType;

    private String lastPrizeType;

    private Integer dicePoints;

    private Integer surplusNumber;

    private Integer nowPosition;

    private Integer lastPosition;
}
