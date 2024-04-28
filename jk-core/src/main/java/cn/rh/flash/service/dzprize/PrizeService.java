package cn.rh.flash.service.dzprize;

import cn.rh.flash.bean.constant.Const;
import cn.rh.flash.bean.entity.dzprize.Prize;
import cn.rh.flash.bean.entity.dzvip.DzRedEnvelopeVipMessage;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzprize.PrizeRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.RedisUtil;
import cn.rh.flash.utils.factory.Page;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrizeService extends BaseService<Prize,Long, PrizeRepository> {

    @Autowired
    private PrizeRepository prizeRepository;

    @Autowired
    private RedisUtil redisUtil;

    //联表查询奖品和中奖信息
    //处理操作奖品中奖用户信息
    public List<Map<String,Object>> queryLuckyDrawList(Page page){
        List<Map> maps = prizeRepository.queryLuckyDrawList();
        List<Map<String,Object>> list = new ArrayList<>();
        for (int i = 0;i<maps.size();i++){
            if (maps.get(i).get("isEnd").equals("1")){
                if (maps.get(i).get("account")==null){
                    HashMap map = new HashMap<>();
                    map.putAll(maps.get(i));
                    String url;
                    url= Const.IMG_ADDR+maps.get(i).get("url").toString();
                    map.put("url",url);
                    list.add(map);
                    continue;
                }
                HashMap map = new HashMap<>();
                String account = maps.get(i).get("account").toString();
                String string = "**"+account.substring((account.length() - 5));
                String url;
                url= Const.IMG_ADDR+maps.get(i).get("url").toString();
                map.putAll(maps.get(i));
                map.put("account",string);
                map.put("url",url);
                list.add(map);
            }else {
                HashMap map = new HashMap<>();
                map.putAll(maps.get(i));
                String url;
                url= Const.IMG_ADDR+maps.get(i).get("url").toString();
                map.put("url",url);
                list.add(map);
            }
        }
        return list;
    }

    //分页查询夺宝奖品
    public List<Prize> queryPrizePageList(Page<Prize> page) {
        String sql = PrizeServiceSql.findPrizeInfoPage(page);
        System.out.println(sql);
        List<Prize> prizeList = (List<Prize>) prizeRepository.queryObjBySql(sql, Prize.class);

        for (int i = 0;i<prizeList.size();i++){
            Prize prize = prizeList.get(i);
            if (prize.getIsEnd().equals("1")){
                String account = prize.getAccount();
                prize.setAccount("**"+account.substring((account.length() - 5)));
                prize.setUrl(Const.IMG_ADDR+prize.getUrl());
            }else {
                prize.setUrl(Const.IMG_ADDR+prize.getUrl());
            }
        }
        return prizeList;
    }


    //查询大富翁奖品列表
    public List<Prize> getMonopolyPrizeList() {
        if (redisUtil.hasKey("getMonopolyPrizeList")){
            Object o = redisUtil.get("getMonopolyPrizeList");
            List<Prize> prizes = JSON.parseObject(o.toString(), new TypeReference<List<Prize>>() {
            });
            return prizes;
        }
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("prizeType","10"));
        List<Prize> prizes = this.queryAll(filters, Sort.by(Sort.Order.asc("participateNumber")));
        redisUtil.set("getMonopolyPrizeList", JSON.toJSONString(prizes),3600);
        return prizes;
    }

    //更新大富翁奖品缓存
    public List<Prize> RefreshMonopolyPrizeList() {
        redisUtil.delete("getMonopolyPrizeList");
        return getMonopolyPrizeList();
    }


    public List<Prize> getRandomRecord(){
        List<Prize> prizes=new ArrayList<>();
        Map<String, String> type = ConstantFactory.me().getDictsToMap("活动类型");
        List<Map> query = (List<Map>) prizeRepository.queryMapBySql(PrizeServiceSql.getRandomRecord());
        for (Map map : query) {
            Prize prize = new Prize();
            prize.setPrizeType(type.get(map.get("prize_type")));
            prize.setPrizeName((String) map.get("prize_name"));
            prizes.add(prize);
        }
        return prizes;
    }






}
