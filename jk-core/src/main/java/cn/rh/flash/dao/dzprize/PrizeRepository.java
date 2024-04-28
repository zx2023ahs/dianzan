package cn.rh.flash.dao.dzprize;

import cn.rh.flash.bean.entity.dzprize.Prize;
import cn.rh.flash.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface PrizeRepository extends BaseRepository<Prize,Long> {

    @Query(value = "SELECT a.idw as idw,a.prize_name as prizeName,a.prize_nice as prizeNice ,a.url as url,a.prize_type as prizeType,a.amount as amount ,a.total_number as totalNumber,a.participate_number as participateNumber,a.is_end as isEnd,b.account as account FROM t_dzprize_prize as a LEFT JOIN t_dzprize_winningrecord as b on a.idw=b.prize_idw where a.prize_type=\"8\"", nativeQuery = true)
    List<Map> queryLuckyDrawList();

}
