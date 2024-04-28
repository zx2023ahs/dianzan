package cn.rh.flash.utils;

import cn.rh.flash.bean.entity.dzprize.Prize;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.vo.SpringContextHolder;
import cn.rh.flash.service.dzprize.PrizeService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.system.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:yangFuYu
 * @Date: 2024/4/2 上午 9:18
 */
public class RandomRecordUtil {


    public static List<String> randomRecordStr() {
        //10条中将记录
        PrizeService prizeService = SpringContextHolder.getBean(PrizeService.class);
        DzVipMessageService vipMessageService = SpringContextHolder.getBean(DzVipMessageService.class);
         List<String > strings = new ArrayList<>();
        for (Prize prize : prizeService.getRandomRecord()) {
            String randomYZMNumber = RandomUtil.getRandomYZMNumber(6);
          //  strings.add("****" + randomYZMNumber + "中奖获得" + prize.getPrizeType() + ":" + prize.getPrizeName());
            strings.add("****" + randomYZMNumber + "中奖获得"  + prize.getPrizeName());
        }
        //5条开通vip记录
        for (DzVipMessage dzVipMessage : vipMessageService.getRandomRecord()) {
            String randomYZMNumber = RandomUtil.getRandomYZMNumber(6);
            strings.add("恭喜 ****" + randomYZMNumber + " 购买"  + dzVipMessage.getName());
        }
        return strings;
    }

}
