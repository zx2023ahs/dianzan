package cn.rh.flash.service.dzpower;


import cn.rh.flash.bean.dto.api.PowerBankVoDto;
import cn.rh.flash.bean.entity.dzpower.PowerBankTask;
import cn.rh.flash.bean.entity.dzpower.PowerReceiveRecord;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzvip.ByVipTotalMoney;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.bean.vo.api.PowerBankTaskVo;
import cn.rh.flash.bean.vo.api.PowerReceiveRecordBean;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.DynamicSpecifications;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.dao.dzpower.PowerBankTaskRepository;
import cn.rh.flash.dao.dzpower.PowerReceiveRecordRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzvip.ByVipTotalMoneyService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.ImageUtil;
import cn.rh.flash.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PowerBankTaskService extends BaseService<PowerBankTask, Long, PowerBankTaskRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PowerBankTaskRepository powerBankTaskRepository;

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private PowerReceiveRecordRepository powerReceiveRecordRepository;

    @Autowired
    private PowerReceiveRecordService powerReceiveRecordService;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private DzVipMessageService dzVipMessageService;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @Autowired
    private ByVipTotalMoneyService byVipTotalMoneyService;

    @Transactional(rollbackFor = Exception.class)
    public void updateVipAmount(DzVipMessage dzVipMessage) {

        List<UserInfo> userInfoList = userInfoService.queryAll(SearchFilter.build("vipType", dzVipMessage.getVipType()));
        Set<Long> ids = userInfoList.stream().map(UserInfo::getId).collect(Collectors.toSet());
        //  当天时间
        String s = DateUtil.getDay() + " " + "00:00:00";
        List<SearchFilter> filters = new ArrayList<>();
        // 过期 < 当天
        filters.add(SearchFilter.build("expireTime", SearchFilter.Operator.GT, DateUtil.parseTime(s)));
        filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, ids));

        // 查询需要修改的返佣任务
        List<PowerBankTask> powerBankTasks = this.queryAll(filters);
        String gear = ConstantFactory.me().getDictsByName("档次类型", dzVipMessage.getGearCode());
        for (PowerBankTask powerBankTask : powerBankTasks) {
            powerBankTask.setPayPrice(dzVipMessage.getDailyIncome());
            powerBankTask.setTotalQuantity(dzVipMessage.getNumberOfTasks());
            powerBankTask.setHours(gear);
            powerBankTask.setVipType(dzVipMessage.getVipType());
            this.update(powerBankTask);
        }
    }


    public List<PowerBankTaskVo> getPowerBankTaskV2(PowerBankVoDto powerBankVoDto, Long uid) {
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
        // 当前时间
        String time = DateUtil.getTime();
        Date date = DateUtil.parse(time, "yyyy-MM-dd HH:mm:ss");

        List<SearchFilter> expireTimeList = new ArrayList<>();
        expireTimeList.add(SearchFilter.build("uid", SearchFilter.Operator.EQ, uid));
        expireTimeList.add(SearchFilter.build("expireTime", gt, date));

        Specification<PowerBankTask> powerBankSpecification = DynamicSpecifications.bySearchFilter(expireTimeList, PowerBankTask.class);


        List<PowerBankTask> powerBankTasks = powerBankTaskRepository.findAll(powerBankSpecification);
        if (powerBankTasks.size() == 0) {
            return new ArrayList<>();
        }
        String collect = powerBankTasks.stream().map(PowerBankTask::getIdw).collect(Collectors.toSet()).toString();
        collect = collect.replace("[", "(").replace("]", ")");
        // 查询已产生的收益
        String sql = PowerReceiveRecordServiceSql.getAmountGroupBy(uid, time, collect);
        List<PowerReceiveRecordBean> sqlBean = (List<PowerReceiveRecordBean>) powerReceiveRecordRepository.queryObjBySql(sql, PowerReceiveRecordBean.class);
        Map<String, Double> map = sqlBean.stream().collect(Collectors.toMap(PowerReceiveRecordBean::getTaskidw, PowerReceiveRecordBean::getMoney));

        // 查询所有vip
        List<DzVipMessage> dzVipMessages = dzVipMessageService.queryAll();
        Map<String, String> vipImgMap = dzVipMessages.stream().collect(Collectors.toMap(DzVipMessage::getVipType, DzVipMessage::getVipImg));
        Map<String, String> powerBankImgMap = dzVipMessages.stream().collect(Collectors.toMap(DzVipMessage::getVipType, DzVipMessage::getPowerBankImg));
        Map<String, String> vipNickNameMap = dzVipMessages.stream().collect(Collectors.toMap(DzVipMessage::getVipType, DzVipMessage::getNick));
        return powerBankTasks.stream().map(v -> {
            PowerBankTaskVo p = new PowerBankTaskVo();
            BeanUtils.copyProperties(v, p);

            if (v.getEndTime() == null) {
                // 已结束
                p.setStatus(2);
            } else {
                boolean after = date.after(v.getEndTime());
                if (!after) {
                    // 营业中
                    p.setStatus(1);
                } else {
                    // 已结束
                    p.setStatus(2);
                }
            }
            p.setDayIncome(p.getPayPrice() * p.getTotalQuantity());
            Double total = (Double) map.get(v.getIdw());
            p.setTotalIncome(total == null ? 0.00 : total);
            p.setFlg(powerBankVoDto.getFlg());
            p.setVipType(v.getVipType());

//            // 是否续费
//            boolean expired = DateUtil.isExpired(apiUserCoom.getVipExpireDate());
//            if (apiUserCoom.getVipType().equals(p.getVipType())  && expired){  // 过期 本级vip可续费
//                p.setFlg(3);
//            }

            p.setVipImage(ImageUtil.getImage(vipImgMap.get(v.getVipType())));
            p.setImage(powerBankImgMap.get(v.getVipType()));
            p.setName(vipNickNameMap.get(v.getVipType()));
            // 计算时间戳
            if (1 == p.getStatus()) {
                p.setTimeStamp(p.getEndTime().getTime() - date.getTime());
            } else {
                p.setStartTime(null);
                p.setTimeStamp(0L);
            }
            return p;
        }).collect(Collectors.toList());

    }

    @Transactional(rollbackFor = Exception.class)
    public Ret startPowerBankTask(String idw, Long uid) {
        // 当前时间
        String time = DateUtil.getTime();
        Date date = DateUtil.parse(time, "yyyy-MM-dd HH:mm:ss");


        UserInfo userInfo = userInfoService.get(uid);
        if (userInfo.getLimitProfit() != 2) {
            return Rets.failure(MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode(), MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED);
        }
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", SearchFilter.Operator.EQ, uid));
        filters.add(SearchFilter.build("idw", SearchFilter.Operator.EQ, idw));
        filters.add(SearchFilter.build("expireTime", SearchFilter.Operator.GT, date));
        PowerBankTask powerBankTask = this.get(filters);
        if (powerBankTask == null) {
            return Rets.failure(MessageTemplateEnum.TASK_INFORMATION_DOES_NOT_EXIST.getCode(), MessageTemplateEnum.TASK_INFORMATION_DOES_NOT_EXIST);
        }

        // 禁止周期内不能运行
        String week = configCache.get(ConfigKeyEnum.PROHIBIT_RELEASE_CYCLE);
        String dayWeek = DateUtil.getWeek(date);
        logger.info("--------当前星期为:{}--------", dayWeek);
        if (!"v0v1".contains(powerBankTask.getVipType())) { // v0v1没有禁止周期

            if (StringUtil.isNotEmpty(week) && week.contains(dayWeek)) {
                return Rets.failure(MessageTemplateEnum.NON_OPERATING_HOURS.getCode(), MessageTemplateEnum.NON_OPERATING_HOURS);
            }
        }


        // 任务运营结束时间不为空 同时当前时间在运营结束之前
        if (powerBankTask.getEndTime() != null && !date.after(powerBankTask.getEndTime())) {
            return Rets.failure(MessageTemplateEnum.TASK_IN_OPERATION.getCode(), MessageTemplateEnum.TASK_IN_OPERATION);
        }
        // 当天 当前充电宝已经运营过 不能再次运营
        if (powerBankTask.getStartTime() != null
                && DateUtil.getDaySub(time, DateUtil.format(powerBankTask.getStartTime(), "yyyy-MM-dd HH:mm:ss")) == 0) {
            logger.info("用户ID:{};------当前充电宝已经运营过不能再次运营------", uid);
            return Rets.failure(MessageTemplateEnum.TASK_NUMS.getCode(), MessageTemplateEnum.TASK_NUMS);
        }

        // 3-9修改逻辑 v1体验会员 当天运营充电宝之后 升级正式会员 还可以运营
        // 查询当天用户有没有除v1体验会员的收益记录
        List<SearchFilter> list = new ArrayList<>();
        list.add(SearchFilter.build("uid", SearchFilter.Operator.EQ, uid));
        list.add(SearchFilter.build("vipType", SearchFilter.Operator.NE, "v1"));
        list.add(SearchFilter.build("startTime", SearchFilter.Operator.LIKEL, DateUtil.getDay()));
        List<PowerReceiveRecord> powerReceiveRecords = powerReceiveRecordService.queryAll(list);
        if (powerReceiveRecords.size() > 0) {
            logger.info("用户ID:{};------当天已有体验会员以外的收益记录------", uid);
            return Rets.failure(MessageTemplateEnum.TASK_NUMS.getCode(), MessageTemplateEnum.TASK_NUMS);
        }

        // 23-4-6 修改 体验会员 体验次数 不超过 会员体验天数 1天 1次 多余不体验
        // 6-5修改逻辑 vip增加最大体验次数(主要为限制某些站v1体验会员) 充电宝有限期内 运营次数不超过最大运营次数 如果运营次数不设置 取有效天数

        if ("v1".equals(powerBankTask.getVipType())) {
            // 查询体验会员体验天数
            DzVipMessage dzVipMessage = dzVipMessageService.get(SearchFilter.build("vipType", "v1"));
            Integer validDate = dzVipMessage.getValidDate(); // 有效天数

            // 如果设置了最大运营次数 就按照最大运营次数走
            if (dzVipMessage.getOperateNum() != null && dzVipMessage.getOperateNum() > 0){
                validDate = dzVipMessage.getOperateNum();
            }

            List<SearchFilter> param = new ArrayList<>();
            param.add(SearchFilter.build("uid", SearchFilter.Operator.EQ, uid));
            param.add(SearchFilter.build("vipType", SearchFilter.Operator.EQ, "v1"));
            List<PowerReceiveRecord> powerReceiveRecordList = powerReceiveRecordService.queryAll(param);
            if (validDate <= powerReceiveRecordList.size()) {
                return Rets.failure(MessageTemplateEnum.TASK_NUMS.getCode(), MessageTemplateEnum.TASK_NUMS);
            }
        }

        // 获取最迟运营时间
        String moveTime = configCache.get(ConfigKeyEnum.LAST_MOVE_TIME);
        Date moveDate = DateUtil.parse(DateUtil.getDay() + " " + (StringUtil.isEmpty(moveTime) ? "23:59:59" : moveTime), "yyyy-MM-dd HH:mm:ss");

        // 如果当前时间在最迟运营时间后
        if (date.after(moveDate)) {
            logger.info("用户ID:{};------已超过最迟运营时间,最迟运营时间:{}------", uid, moveDate);
            return Rets.failure(MessageTemplateEnum.EQUIPMENT_RETURNING.getCode(), MessageTemplateEnum.EQUIPMENT_RETURNING);
        }

        logger.info("用户ID:{};------运营开始时间:{}------", uid, time);

        Date endDayDate = DateUtil.parse(DateUtil.getDay() + " " + "23:59:59", "yyyy-MM-dd HH:mm:ss");
        Date endDate = powerReceiveRecordService.addPowerReceiveRecord(powerBankTask, date, endDayDate,userInfo.getVipType());
        if (endDate != null) {
            powerBankTask.setStartTime(date);
            powerBankTask.setEndTime(endDate);
            this.update(powerBankTask);
        }
        logger.info("用户ID:{};------运营结束时间:{}------", uid, DateUtil.format(endDate, "yyyy-MM-dd HH:mm:ss"));
        return Rets.success();
    }


    public List<PowerBankTask> findCancelRefund(int page, int pageSize) {
        String sql = PowerReceiveRecordServiceSql.findCancelRefund(page, pageSize);
        List<PowerBankTask> powerBankTasks = (List<PowerBankTask>) powerBankTaskRepository.queryObjBySql(sql, PowerBankTask.class);
//        List<PowerBankTask> powerBankTasks = powerBankTaskRepository.queryBySql(sql);
        return powerBankTasks;
    }

    /** 
    * @Description: 修改vip到期时间 同步充电宝任务到期时间 
    * @Param:  
    * @return:  
    * @Author: Skj
    */
    @Transactional
    public void updateVipExpireDate(UserInfo userInfo, Date vipExpireDate) {

        //  当天时间
        String s = DateUtil.getDay() + " " + "00:00:00";
        // 查询当前用户有没有未过期充电宝任务
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", userInfo.getId()));
        // 过期 < 当天
        filters.add(SearchFilter.build("expireTime", SearchFilter.Operator.GT, DateUtil.parseTime(s)));
        List<PowerBankTask> powerBankTasks = this.queryAll(filters);
        for (PowerBankTask powerBankTask : powerBankTasks) {
            if ("v0".equals(userInfo.getVipType())) {
                // 手动调整成v0 直接清空用户累计购买VIP金额(所有手动操作的东西 都需要去手动退)
                ByVipTotalMoney byVipTotalMoney = byVipTotalMoneyService.get(SearchFilter.build("uid", powerBankTask.getUid()));
                if (byVipTotalMoney!=null){
                    byVipTotalMoney.setTotalMoney(0.00);
                    byVipTotalMoneyService.update(byVipTotalMoney);
                }else {
                    byVipTotalMoney = new ByVipTotalMoney();
                    byVipTotalMoney.setIdw(new IdWorker().nextId()+"");
                    byVipTotalMoney.setSourceInvitationCode(userInfo.getSourceInvitationCode());
                    byVipTotalMoney.setTotalMoney(0.00);
                    byVipTotalMoney.setUid(userInfo.getId());
                    byVipTotalMoney.setAccount(userInfo.getAccount());
                    byVipTotalMoneyService.insert(byVipTotalMoney);
                }
            }
            powerBankTask.setExpireTime(vipExpireDate);
            powerBankTask.setIsRefund(2);
            this.update(powerBankTask);
        }
    }
}

