package cn.rh.flash.service.dzpower;

import cn.rh.flash.bean.entity.dzpower.FlowingWaterPb;
import cn.rh.flash.dao.dzpower.FlowingWaterPbRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowingWaterPbService extends BaseService<FlowingWaterPb,Long, FlowingWaterPbRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FlowingWaterPbRepository flowingWaterPbRepository;

    /**
     * 条件查询 当前用户最后一条返佣记录
     * @param uid
     * @param taskIdw
     */
    public FlowingWaterPb getFlowingWaterPb(Long uid, String taskIdw) {

        String sql = FlowingWaterPbServiceSql.getFlowingWaterPb(uid,taskIdw);

        List<FlowingWaterPb> flowingWaterPbs = (List<FlowingWaterPb>) flowingWaterPbRepository.query(sql);
        if (flowingWaterPbs.size()>0){
            return flowingWaterPbs.get(0);
        }
        return null;
    }
}
