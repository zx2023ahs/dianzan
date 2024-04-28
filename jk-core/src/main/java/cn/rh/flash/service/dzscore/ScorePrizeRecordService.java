package cn.rh.flash.service.dzscore;

import cn.rh.flash.bean.dto.api.ScoreDto;
import cn.rh.flash.bean.entity.dzscore.ScorePrizeRecord;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzscore.ScorePrizeRecordRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.utils.factory.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScorePrizeRecordService extends BaseService<ScorePrizeRecord, Long, ScorePrizeRecordRepository> {

    public Ret findScorePrizeRecordByUser(ScoreDto page, Long userId) {//积分类型分类
        Page<ScorePrizeRecord> pageFilters = new Page<>(page.getCurrent(), page.getSize());
        pageFilters.addFilter(SearchFilter.build("uid", userId));
        pageFilters.addFilter(SearchFilter.build("prizeType", page.getPrizeType()));
        List<ScorePrizeRecord> scorePrizeRecords = this.queryPage(pageFilters).getRecords();
        return Rets.success(scorePrizeRecords);
    }
}
