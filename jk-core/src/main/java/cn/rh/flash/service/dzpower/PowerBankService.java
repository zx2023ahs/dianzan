package cn.rh.flash.service.dzpower;


import cn.rh.flash.bean.dto.api.PowerBankVoDto;
import cn.rh.flash.bean.entity.dzcredit.UserCredit;
import cn.rh.flash.bean.entity.dzpower.PowerBank;
import cn.rh.flash.bean.entity.dzpower.PowerBankTask;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzvip.ByVipTotalMoney;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.bean.vo.api.PowerBankVo;
import cn.rh.flash.bean.vo.query.DynamicSpecifications;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzpower.PowerBankRepository;
import cn.rh.flash.dao.dzpower.PowerBankTaskRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ShopService;
import cn.rh.flash.service.dzcredit.UserCreditService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzvip.ByVipTotalMoneyService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PowerBankService extends BaseService<PowerBank, Long, PowerBankRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PowerBankRepository powerBankRepository;

    @Autowired
    private PowerBankTaskRepository powerBankTaskRepository;

    @Autowired
    private DzVipMessageService dzVipMessageService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private ByVipTotalMoneyService byVipTotalMoneyService;

    @Autowired
    private UserCreditService userCreditService;

    @Autowired
    private PowerBankService powerBankService;

    public List<PowerBankVo> getListForPowerBankVo(Integer dzstatus) {

        Specification<PowerBank> powerBankSpecification = DynamicSpecifications.bySearchFilter(SearchFilter.build("dzstatus", dzstatus), PowerBank.class);

        return powerBankRepository.findAll(powerBankSpecification).stream().map(v -> {
            PowerBankVo p = new PowerBankVo();
            BeanUtils.copyProperties(v, p);
            p.setFlg(1);
            return p;
        }).collect(Collectors.toList());
    }


    public Object getPowerBankRebateRecord(PowerBankVoDto powerBankVoDto, Long uid) {


        SearchFilter.Operator gt;
        switch (powerBankVoDto.getFlg()) {
            case 1:
                gt = SearchFilter.Operator.GTE;  // 大于等于
                break;
            case 2:
                gt = SearchFilter.Operator.LT;
                break;
            default:
                gt = null;
        }
        if (gt == null) {
            throw new ApiException(MessageTemplateEnum.PARAM_NOT_EXIST);
        }

        List<SearchFilter> expireTimeList = new ArrayList<>();
        expireTimeList.add(SearchFilter.build("uid", SearchFilter.Operator.EQ, uid));
        expireTimeList.add(SearchFilter.build("expireTime", gt, DateUtil.parseTime(DateUtil.getDay() + " " + "00:00:00")));

        Specification<PowerBankTask> powerBankSpecification = DynamicSpecifications.bySearchFilter(expireTimeList, PowerBankTask.class);

        return powerBankTaskRepository.findAll(powerBankSpecification).stream().map(v -> {
            PowerBankVo p = new PowerBankVo();
            BeanUtils.copyProperties(v, p);
//            String powerBankImg = dzVipMessageService.findVipImg(v.getUid());
//            if (StringUtil.isNotEmpty(powerBankImg)){
//                p.setImage(powerBankImg);
//            }
            p.setPrice(v.getPayPrice());
            p.setFlg(powerBankVoDto.getFlg());
            return p;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Integer updateVipType(String type, UserInfo userInfo, Long uid) { // 0 新增  1 修改

        DzVipMessage dzVipMessage = dzVipMessageService.get(SearchFilter.build("vipType", userInfo.getVipType()));

        if ("1".equals(type)) { // 修改
            //  当天时间
            String s = DateUtil.getDay() + " " + "00:00:00";
            // 查询当前用户有没有未过期充电宝任务
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("uid", uid));
            // 过期 < 当天
            filters.add(SearchFilter.build("expireTime", SearchFilter.Operator.GT, DateUtil.parseTime(s)));
            Specification<PowerBankTask> powerBankSpecification = DynamicSpecifications.bySearchFilter(filters, PowerBankTask.class);
            List<PowerBankTask> powerBankTasks = powerBankTaskRepository.findAll(powerBankSpecification);
            if (powerBankTasks.size() == 0) {
                if (!"v0".equals(userInfo.getVipType())) {
                    shopService.buyPowerBank(userInfo.getSourceInvitationCode(),
                            userInfo.getId(), userInfo.getAccount(), dzVipMessage.getNumberOfTasks(),
                            dzVipMessage.getValidDate(), dzVipMessage.getDailyIncome(), dzVipMessage.getVipType());
                }
            } else {
                for (PowerBankTask powerBankTask : powerBankTasks) {
                    if ("v0".equals(userInfo.getVipType())) {
                        shopService.overPowerBank(uid, "-1");
                        // 手动调整成v0 直接清空用户累计购买VIP金额(所有手动操作的东西 都需要去手动退)
                        ByVipTotalMoney byVipTotalMoney = byVipTotalMoneyService.get(SearchFilter.build("uid", powerBankTask.getUid()));
                        if (byVipTotalMoney != null) {
                            byVipTotalMoney.setTotalMoney(0.00);
                            byVipTotalMoneyService.update(byVipTotalMoney);
                        } else {
                            byVipTotalMoney = new ByVipTotalMoney();
                            byVipTotalMoney.setIdw(new IdWorker().nextId() + "");
                            byVipTotalMoney.setSourceInvitationCode(userInfo.getSourceInvitationCode());
                            byVipTotalMoney.setTotalMoney(0.00);
                            byVipTotalMoney.setUid(userInfo.getId());
                            byVipTotalMoney.setAccount(userInfo.getAccount());
                            byVipTotalMoneyService.insert(byVipTotalMoney);
                        }
                    } else {
                        powerBankTask.setPayPrice(dzVipMessage.getDailyIncome());
                        powerBankTask.setTotalQuantity(dzVipMessage.getNumberOfTasks());
                        powerBankTask.setVipType(userInfo.getVipType());
                        String gear = ConstantFactory.me().getDictsByName("档次类型", dzVipMessage.getGearCode());
                        powerBankTask.setHours(gear);
                        PowerBank powerBank = powerBankService.get(SearchFilter.build("name", dzVipMessage.getNick()));

                        if (powerBank != null) {
                            powerBankTask.setPbidw(powerBank.getIdw());
                            powerBankTask.setExpireTime(userInfo.getVipExpireDate());
                            powerBankTask.setName(dzVipMessage.getNick());
                            powerBankTask.setImage(dzVipMessage.getPowerBankImg());
                            powerBankTaskRepository.save(powerBankTask);
                        }
                    }
                }
            }

            // 修改用户信誉分 vip类型
            UserCredit userCredit = userCreditService.get(SearchFilter.build("uid", userInfo.getId()));
            if (userCredit!=null){
                userCredit.setVipType(userInfo.getVipType());
                userCreditService.update(userCredit);
            }

            /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();*/
            String format = DateUtil.getTime();
        } else {
            if (!"v0".equals(userInfo.getVipType())) {
                shopService.buyPowerBank(userInfo.getSourceInvitationCode(),
                        userInfo.getId(), userInfo.getAccount(), dzVipMessage.getNumberOfTasks(),
                        dzVipMessage.getValidDate(), dzVipMessage.getDailyIncome(), dzVipMessage.getVipType());
            }
        }

        return dzVipMessage == null ? -1 : dzVipMessage.getValidDate();
    }

}

