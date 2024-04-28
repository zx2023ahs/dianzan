package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.entity.dzsys.HomePageTotal;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzsys.HomePageTotalRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomePageTotalService extends BaseService<HomePageTotal,Long,HomePageTotalRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private HomePageTotalRepository homePageTotalRepository;

    public List<HomePageTotal> getDayReport(String startDay, String endDay,String ucode) {

        // 获取两个日期之间的日期
        List<String> betWeenDate = DateUtil.getBetWeenDate(startDay, endDay);
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("sourceInvitationCode",ucode));
        filters.add(SearchFilter.build("day", SearchFilter.Operator.IN, betWeenDate));
        List<HomePageTotal> homePageTotals = this.queryAll(filters, Sort.by(Sort.Direction.DESC,"day"));
        return homePageTotals;
    }

}

