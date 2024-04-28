package cn.rh.flash.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class MyToolBiz {
    /**
     * 抽中奖
     * @param map
     * @return
     */
    public static String weightRandom(Map<String, String> map) {
        Set<String> keySet = map.keySet();
        List<String> weights = new ArrayList<>();
        for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
            String weightStr = it.next();
            String weight = map.get(weightStr);
            try {
                long round = Math.round(Double.parseDouble(weight));
                for (int i = 0; i < ( round ) ; i++) {
                    weights.add( weightStr);
                }
            }catch ( Exception e){
                log.warn( "{}-{}",weight,e.getMessage() );
            }
        }
        if (weights.size() == 0 ) {
            return "";
        }
        int idx = new Random().nextInt(weights.size());
        return weights.get(idx);

    }

}
