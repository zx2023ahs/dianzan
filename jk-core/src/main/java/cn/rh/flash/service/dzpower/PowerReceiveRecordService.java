package cn.rh.flash.service.dzpower;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.rh.flash.bean.dto.api.ReceiveRecordDto;
import cn.rh.flash.bean.entity.dzcredit.CreditConfig;
import cn.rh.flash.bean.entity.dzcredit.UserCredit;
import cn.rh.flash.bean.entity.dzpower.PowerBankTask;
import cn.rh.flash.bean.entity.dzpower.PowerReceiveRecord;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.vo.api.PowerReceiveRecordVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzpower.PowerReceiveRecordRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzcredit.CreditConfigService;
import cn.rh.flash.service.dzcredit.UserCreditService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.EasyExcelUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.ImageUtil;
import cn.rh.flash.utils.factory.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PowerReceiveRecordService extends BaseService<PowerReceiveRecord, Long, PowerReceiveRecordRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PowerReceiveRecordService powerReceiveRecordService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private DzVipMessageService dzVipMessageService;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private UserCreditService userCreditService;

    @Autowired
    private CreditConfigService creditConfigService;

    @Transactional(rollbackFor = Exception.class)
    public Date addPowerReceiveRecord(PowerBankTask powerBankTask, Date date, Date endDayDate,String vipType) {
        // 获取当天剩余小时  不足一小时按照一小时算  如果当天结束时间超过任务过期时间 就按照 任务过期时间算
        Long hour = getDaySurHour(Long.valueOf(powerBankTask.getHours()),
                date, endDayDate.after(powerBankTask.getExpireTime()) ? powerBankTask.getExpireTime() : endDayDate);

        // 查询当前用户信誉分
        UserCredit userCredit = userCreditService.get(SearchFilter.build("uid", powerBankTask.getUid()));
        userCredit.setStatus("1");
        userCredit.setFinalDate(DateUtil.format(date, "yyyy-MM-dd"));
        userCredit.setVipType(vipType);

        // 根据信誉分 查询收益率
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("creditMin", SearchFilter.Operator.LTE, userCredit.getCredit()));
        filters.add(SearchFilter.build("creditMax", SearchFilter.Operator.GTE, userCredit.getCredit()));
        CreditConfig creditConfig = creditConfigService.get(filters);
        Double yield = 1.00;
        if (creditConfig != null) {
            yield = creditConfig.getYield();
        }
        // 计算任务结束时间
        LocalDateTime localDateTime = DateUtil.getLocalDateTime(date);
        Date afterDate = DateUtil.localDateTimeToDate(localDateTime.plusHours(hour));

        PowerReceiveRecord powerReceiveRecord = new PowerReceiveRecord();
        powerReceiveRecord.setIdw(new IdWorker().nextId() + "");
        powerReceiveRecord.setSourceInvitationCode(powerBankTask.getSourceInvitationCode());
        powerReceiveRecord.setUid(powerBankTask.getUid());
        powerReceiveRecord.setAccount(powerBankTask.getAccount());
        powerReceiveRecord.setTaskidw(powerBankTask.getIdw());
        powerReceiveRecord.setPbidw(powerBankTask.getPbidw());
        powerReceiveRecord.setImage(powerBankTask.getImage());
        powerReceiveRecord.setName(powerBankTask.getName());
        // 小时价 = 单价÷档次小时
        powerReceiveRecord.setPayPrice(powerBankTask.getPayPrice() / Integer.valueOf(powerBankTask.getHours()));
        powerReceiveRecord.setTotalQuantity(powerBankTask.getTotalQuantity());
        powerReceiveRecord.setIncomeHour(hour);
        powerReceiveRecord.setStatus(1);
        // 当前设备收益 = 小时价*数量*运行小时   5-17 当前设备收益 = 小时价*数量*运行小时*收益率
        powerReceiveRecord.setMoney(powerReceiveRecord.getPayPrice() * powerReceiveRecord.getTotalQuantity() * powerReceiveRecord.getIncomeHour()*yield);
        powerReceiveRecord.setStartTime(date);
        powerReceiveRecord.setEndTime(afterDate);
        powerReceiveRecord.setVipType(powerBankTask.getVipType());

        powerReceiveRecord.setCredit(userCredit.getCredit());
        powerReceiveRecord.setYield(yield);

        this.insert(powerReceiveRecord);
        return afterDate;
    }

    // 计算 任务小时
    private Long getDaySurHour(Long hours, Date date, Date endDayDate) {
        long h = 1000 * 60 * 60;
        long between = endDayDate.getTime() - date.getTime();

        long num = between / h;
        if (between % h > 0) {
            num++;
        }
        // 如果剩余小时超过档位小时 按照档位小时来
        return num > hours ? hours : num;
    }


    public Ret getReceiveRecord(ReceiveRecordDto receiveRecordDto, Long userId) {
        Page<PowerReceiveRecord> pageFilters = new Page<>(receiveRecordDto.getPageNo(), receiveRecordDto.getPageSize());
        pageFilters.addFilter("taskidw", receiveRecordDto.getTaskidw());
        pageFilters.addFilter("uid", userId);
//        pageFilters.addFilter("endTime",SearchFilter.Operator.LT,DateUtil.parse(DateUtil.getTime(),"yyyy-MM-dd HH:mm:ss"));


        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.asc("status"));
        orders.add(Sort.Order.desc("createTime"));
        pageFilters.setSort(Sort.by(orders));
        pageFilters = this.queryPage(pageFilters);
        List<PowerReceiveRecord> records = pageFilters.getRecords();
        // 当前时间
        String time = DateUtil.getTime();
        Date date = DateUtil.parse(time, "yyyy-MM-dd HH:mm:ss");
        // 查询所有vip
        List<DzVipMessage> dzVipMessages = dzVipMessageService.queryAll();
        Map<String, String> vipImgMap = dzVipMessages.stream().collect(Collectors.toMap(DzVipMessage::getVipType, DzVipMessage::getVipImg));
        Map<String, String> powerBankImgMap = dzVipMessages.stream().collect(Collectors.toMap(DzVipMessage::getVipType, DzVipMessage::getPowerBankImg));
        Map<String, String> vipNickNameMap = dzVipMessages.stream().collect(Collectors.toMap(DzVipMessage::getVipType, DzVipMessage::getNick));
        List<PowerReceiveRecordVo> powerReceiveRecordVos = records.stream().map(v -> {
            PowerReceiveRecordVo p = new PowerReceiveRecordVo();
            BeanUtils.copyProperties(v, p);
            p.setVipType(v.getVipType().replace("v", ""));
            p.setVipImage(ImageUtil.getImage(vipImgMap.get(v.getVipType())));
            p.setImage(powerBankImgMap.get(v.getVipType()));
            p.setName(vipNickNameMap.get(v.getVipType()));
//            boolean betWeen = DateUtil.betWeen(date, v.getStartTime(), v.getEndTime());
            boolean after = date.after(v.getEndTime());
            if (after) {
                p.setFlg(2);
            } else {
                p.setFlg(1);
            }
            return p;
        }).collect(Collectors.toList());
        return Rets.success(powerReceiveRecordVos);
    }

    @Transactional(rollbackFor = Exception.class)
    public Ret drawIncome(List<String> idws, Long userId) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", SearchFilter.Operator.EQ, userId));
        filters.add(SearchFilter.build("idw", SearchFilter.Operator.IN, idws));
//        filters.add(SearchFilter.build("status", SearchFilter.Operator.EQ, 1));
        List<PowerReceiveRecord> powerReceiveRecords = this.queryAll(filters);

        UserInfo userInfo = userInfoService.get(userId);

//        Double money = 0.00;
//        Integer num = 0;
        // 当前时间
        Date date = DateUtil.parseTime(DateUtil.getTime());
        for (PowerReceiveRecord powerReceiveRecord : powerReceiveRecords) {
            // 状态等于已领取 不执行下面了
            if (powerReceiveRecord.getStatus() == 2) {
                continue;
            }
            // 状态为营业中 直接跳过当前记录
            boolean after = date.after(powerReceiveRecord.getEndTime());
            if (!after) {
                continue;
            }
//            // 计数 多少个未领取的
//            num++;
//            // 合计 多少钱
//            money = money + powerReceiveRecord.getMoney();
//            powerReceiveRecord.setStatus(2);
//            this.update(powerReceiveRecord);

            // 给自己反
            recordInformation.addRecordPb(powerReceiveRecord.getMoney(),
                    userInfo.getId(),
                    userInfo.getSourceInvitationCode(),
                    userInfo.getAccount(),
                    date,
                    userInfo.getAccount(),
                    0,
                    userInfo.getAccount());

//            Map<String, DzVipMessage> vipMap = getVipList();
            //  l1.id,l1.account,l1.fee,  l2.id,l2.account,l2.fee  ,l3.id,l3.account,l3.fee
//            Map upUpUp = userInfoService.findUpUpUpCdb(userInfo);
            // 给上级反
//            if (upUpUp != null) {
//                if (upUpUp.get("l1id") != null && !((upUpUp.get("l1id") + "").equals(userInfo.getId() + ""))) {
//                    DzVipMessage dzVipMessage = getMinVip(userInfo.getVipType(), upUpUp.get("l1vipType").toString(), vipMap);
//                    if (dzVipMessage != null && dzVipMessage.getL1TaskRebate() > 0) {
//                        recordInformation.addRecordPb(BigDecimalUtils.multiply(powerReceiveRecord.getMoney(), dzVipMessage.getL1TaskRebate()),
//                                Long.valueOf(upUpUp.get("l1id").toString()),
//                                userInfo.getSourceInvitationCode(),
//                                upUpUp.get("l1account").toString(),
//                                date,
//                                upUpUp.get("l1account").toString(),
//                                1,
//                                userInfo.getAccount());
//                    }
//                }
//                if (upUpUp.get("l2id") != null && !((upUpUp.get("l2id") + "").equals((upUpUp.get("l1id") + "")))) {
//                    DzVipMessage dzVipMessage = getMinVip(userInfo.getVipType(), upUpUp.get("l2vipType").toString(), vipMap);
//                    if (dzVipMessage != null && dzVipMessage.getL2TaskRebate() > 0) {
//                        recordInformation.addRecordPb(BigDecimalUtils.multiply(powerReceiveRecord.getMoney(), dzVipMessage.getL2TaskRebate()),
//                                Long.valueOf(upUpUp.get("l2id").toString()),
//                                userInfo.getSourceInvitationCode(),
//                                upUpUp.get("l2account").toString(),
//                                date,
//                                upUpUp.get("l2account").toString(),
//                                2,
//                                userInfo.getAccount());
//                    }
//                }
//                if (upUpUp.get("l3id") != null && !((upUpUp.get("l3id") + "").equals((upUpUp.get("l2id") + "")))) {
//                    DzVipMessage dzVipMessage = getMinVip(userInfo.getVipType(), upUpUp.get("l3vipType").toString(), vipMap);
//                    if (dzVipMessage != null && dzVipMessage.getL3TaskRebate() > 0) {
//                        recordInformation.addRecordPb(BigDecimalUtils.multiply(powerReceiveRecord.getMoney(), dzVipMessage.getL3TaskRebate()),
//                                Long.valueOf(upUpUp.get("l3id").toString()),
//                                userInfo.getSourceInvitationCode(),
//                                upUpUp.get("l3account").toString(),
//                                date,
//                                upUpUp.get("l3account").toString(),
//                                3,
//                                userInfo.getAccount());
//                    }
//                }
//            }
            powerReceiveRecord.setStatus(2);
            this.update(powerReceiveRecord);

        }

//        logger.info("--------用户:{},一键领取:{}条,合计金额:{}--------", userInfo.getAccount(), num, money);
//        // 没有待领取的 或者领取金额为0
//        if (num == 0 || money == 0.00) {
//            return Rets.success();
//        }

        return Rets.success();
    }

    private DzVipMessage getMinVip(String vip, String upVip, Map<String, DzVipMessage> vipMap) {
        int vipInt = Integer.parseInt(vip.replace("v", ""));
        int upVipInt = Integer.parseInt(upVip.replace("v", ""));

        if (vipInt > upVipInt) {
            return vipMap.get(upVip);
        }

        return vipMap.get(vip);
    }

    private Map<String, DzVipMessage> getVipList() {
        List<DzVipMessage> dzVipMessagesList = dzVipMessageService.queryAll();
        Map<String, DzVipMessage> map = dzVipMessagesList.stream().collect(Collectors.toMap(DzVipMessage::getVipType, Function.identity()));
        return map;
    }


    public void exportV2(HttpServletResponse response, List<PowerReceiveRecord> records) {
        Map<String, String> status = ConstantFactory.me().getDictsToMap("领取状态");
        Map<String, String> vipType = ConstantFactory.me().getDictsToMap("ViP类型");
        List<cn.rh.flash.bean.vo.dzpower.PowerReceiveRecordVo> voList=new ArrayList<>();
        for (PowerReceiveRecord record : records) {
            cn.rh.flash.bean.vo.dzpower.PowerReceiveRecordVo vo=new cn.rh.flash.bean.vo.dzpower.PowerReceiveRecordVo();
            BeanUtils.copyProperties(record,vo);
            if(ObjectUtil.isNotEmpty(record.getUser()) && StrUtil.isNotEmpty(record.getUser().getAccount())){
                vo.setUserAccount(record.getUser().getAccount());
            }
            if (ObjectUtil.isNotEmpty(status) && ObjectUtil.isNotEmpty(record.getStatus()) && StrUtil.isNotEmpty(status.get(record.getStatus().toString()))){
                vo.setStatusName(status.get(record.getStatus().toString()));
            }
            boolean after = DateUtil.parse(DateUtil.getTime(), "yyyy-MM-dd HH:mm:ss").after((Date) record.getEndTime());
            if (!after) {
                // 营业中
                vo.setFlgName("营业中");
            } else {
                vo.setFlgName("已结束");
            }
            if (ObjectUtil.isNotEmpty(vipType) && StrUtil.isNotEmpty(vipType.get(record.getVipType()))){
                vo.setVipTypeName(vipType.get(record.getVipType()));
            }
            voList.add(vo);
        }
        EasyExcelUtil.export(response,"收益记录",voList,cn.rh.flash.bean.vo.dzpower.PowerReceiveRecordVo.class);
    }

}
