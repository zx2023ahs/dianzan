package cn.rh.flash.service.dzvip;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzvip.VipPurchaseHistory;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.vo.dzvip.VipPurchaseHistoryVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzuser.UserInfoRepository;
import cn.rh.flash.dao.dzvip.VipPurchaseHistoryRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.dzsys.PaymentChannelService;
import cn.rh.flash.service.dzuser.RechargeRecordService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.EasyExcelUtil;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VipPurchaseHistoryService extends BaseService<VipPurchaseHistory,Long,VipPurchaseHistoryRepository>  {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private VipPurchaseHistoryRepository vipPurchaseHistoryRepository;
    @Autowired
    private RechargeRecordService rechargeRecordService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PaymentChannelService paymentChannelService;

    public void exportV2(HttpServletResponse response, List<Map<String, Object>> list) {
        List<VipPurchaseHistoryVo> voList=new ArrayList<>();
        for (Map<String, Object> stringObjectMap : list) {
            VipPurchaseHistoryVo vo=new VipPurchaseHistoryVo();
            BeanUtil.mapToBean(stringObjectMap, vo);
            User user = BeanUtil.objToBean(stringObjectMap.get("user"), User.class);
            if (ObjUtil.isNotEmpty(stringObjectMap.get("user")) && ObjUtil.isNotEmpty(user)){
                vo.setUserAccount(user.getAccount());
            }
            if (ObjUtil.isNotEmpty(stringObjectMap.get("previousViPType_str"))){
                vo.setPreviousViPTypeName(stringObjectMap.get("previousViPType_str").toString());
            }
            if (ObjUtil.isNotEmpty(stringObjectMap.get("afterViPType_str"))){
                vo.setAfterViPTypeName(stringObjectMap.get("afterViPType_str").toString());
            }
            if (ObjUtil.isNotEmpty(stringObjectMap.get("paymentMethod_str"))){
                vo.setPaymentMethodName(stringObjectMap.get("paymentMethod_str").toString());
            }
            if (ObjUtil.isNotEmpty(stringObjectMap.get("whetherToPay_str"))){
                vo.setWhetherToPayName(stringObjectMap.get("whetherToPay_str").toString());
            }
            if (ObjUtil.isNotEmpty(stringObjectMap.get("expireDate"))){
                vo.setExpireDateToDate(DateUtil.parse(stringObjectMap.get("expireDate").toString(),"yyyy-MM-dd HH:mm:ss"));
            }
            voList.add(vo);
        }
        EasyExcelUtil.export(response,"vip购买记录",voList,VipPurchaseHistoryVo.class);
    }

    public Ret examine(VipPurchaseHistory vipPurchaseHistory, boolean isProxy, String ucode) {
       // 支付状态 1:未支付,2:已支付
        if (ObjUtil.isEmpty(vipPurchaseHistory.getPaymentAmount()) || vipPurchaseHistory.getPaymentAmount()<0) {
            return Rets.failure("金额输入错误!");
        }
        if (ObjUtil.isEmpty(vipPurchaseHistory.getDepositAddress())) {
            return Rets.failure("地址输入错误!");
        }
        if (vipPurchaseHistory.getWhetherToPay()!=2) {
            return Rets.failure("不是有效的状态值!");
        }
        val update = get(vipPurchaseHistory.getId());
        if (ObjectUtil.isEmpty(update)) {
            return Rets.failure("数据不存在!");
        }
        if (update.getWhetherToPay()==2 ) {
            return Rets.failure("已经审核过!");
        }
        if (!"bank".equals(update.getChannelType())  ){
            return Rets.failure("不是可审核的vip购买记录!");
        }
        if (!ucode.equals ("admin")){
            if (isProxy && !update.getSourceInvitationCode().equals(ucode)){
                return Rets.failure("不是你代理的账号,无权限!");
            }
        }
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", update.getChannelName()));
        if (ObjUtil.isEmpty(paymentChannel)) {
            return Rets.failure(update.getChannelName()+"通道信息不存在!");
        }
        UserInfo one = userInfoRepository.getOne(update.getUid());
        if (ObjUtil.isEmpty(one)) {
            return Rets.failure("用户信息不存在!");
        }
        rechargeRecordService.payVipSuccess(vipPurchaseHistory.getPaymentAmount().toString(), vipPurchaseHistory.getDepositAddress(), update, paymentChannel, one);
        return Rets.success();
    }
}

