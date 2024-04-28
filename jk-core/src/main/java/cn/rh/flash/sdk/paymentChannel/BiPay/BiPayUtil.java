package cn.rh.flash.sdk.paymentChannel.BiPay;

import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean.*;
import cn.rh.flash.utils.*;
import com.sun.istack.Nullable;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Map;

@Log4j2
public class BiPayUtil {

    //private static final String URL = "https://aa.im"; // 后台获取

    private static final String RECHARGE_URL = "/api/bipay/gateway/pay/create";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/notifyRechargeOrderVIP";

    private static final String WITHDRAW_URL = "/api/bipay/gateway/withdraw/create";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/notifyWithdrawOrder";

    //private static final String SECRET_KEY = "2605baaf2acbfbe5a34303cb1f0cc6fa";  // 后台获取

    //private static final String USER_ID = "701567539364233216";  // 后台获取

    public static void main(String[] args) throws Exception {

//        RechargeParam rechargeParam = new RechargeParam();
//        rechargeParam.setCustomId("0");
//        rechargeParam.setAmount("10");
//        rechargeParam.setCustomOrderId(MakeOrderNum.makeOrderNum("CZ"));
//        System.out.println(createRechargeOrder(rechargeParam));


//        String json = "{ " +
//                "  \"address\":\"TT6QhzFRheTD4vsfNpu8AwFN8RnTuvXCGE\",  " +
//                "  \"amount\":\"1.0\",                                   " +
//                "  \"coinAmount\":\"1.0\",                               " +
//                "  \"coinCode\":\"USDT.TRC20\",                         " +
//                "  \"currency\":\"USD\",                                " +
//                "  \"customId\":\"0\",                                 " +
//                "  \"customOrderId\":\"1632474850130\",                " +
//                "  \"id\":\"648209760893534208\",                        " +
//                "  \"remarks\":\"测试\",                                " +
//                "  \"sign\":\"c5468b5302b6ba0c5508e1e383543f29\",        " +
//                "  \"status\":\"0\"                                     " +
//                "}";
//
//        System.out.println(rechargeOrderNotify(json));

//        WithdrawParam withdrawParam = new WithdrawParam();
//        withdrawParam.setUserId("645677562713123456");
//        withdrawParam.setAmount("1.0");
//        //Tron钱包地址
//        withdrawParam.setAddress("TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t");
//        withdrawParam.setNotifyUrl(WITHDRAW_NOTIFY_URL);
//        withdrawParam.setCustomOrderId(MakeOrderNum.makeOrderNum("TX"));
//
//        System.out.println(createWithdrawOrder(withdrawParam));

//        String json = "{ " +
//                "    \"id\": \"a862eb20fcbd487f96ba478509200f7d\",        " +
//                "    \"customOrderId\": \"1644822726018\",                " +
//                "    \"address\": \"TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t\", " +
//                "    \"amount\": \"1.000\",                               " +
//                "    \"fee\": \"0.015\",                                  " +
//                "    \"status\": 0,                                    " +
//                "    \"statusMsg\": \"审核中\",                            " +
//                "    \"sign\": \"1d4b3d323742ebc3c6f24ecf7f580eb4\"      " +
//                "  }";
//
//        System.out.println(withdrawOrderNotify(json));

    }

    /**
     * 充值支付订单
     *
     * @param rechargeParam
     * @return
     */
    @Nullable
    public static RechargeResp createRechargeOrder(RechargeParam rechargeParam, PaymentChannel paymentChannel) {
        return getRechargeResp(rechargeParam, HttpUtil.getBtServerName() + RECHARGE_NOTIFY_URL, paymentChannel.getCurrencyCode(), paymentChannel.getDzkey(), paymentChannel.getPrivateKey());
    }

    /**
     * 购买vip支付订单
     *
     * @param rechargeParam
     * @return
     */
    @Nullable
    public static RechargeResp createRechargeOrderVIP(RechargeParam rechargeParam, PaymentChannel paymentChannel) {
        return getRechargeResp(rechargeParam, HttpUtil.getBtServerName() + RECHARGE_NOTIFY_VIP_URL, paymentChannel.getCurrencyCode(), paymentChannel.getDzkey(), paymentChannel.getPrivateKey());
    }

    /*
     * 创建 支付订单
     * @param rechargeParam
     * @param rechargeNotifyUrl  回调地址
     * @return
     */
    private static RechargeResp getRechargeResp(RechargeParam rechargeParam, String rechargeNotifyUrl, String user_id, String url, String secret_key) {
        rechargeParam.setCurrency("USD");
        rechargeParam.setUserId(user_id);
//        rechargeParam.setCoinCode("USDT.TRC20");
        //修改通道类型
        if (StringUtil.isEmpty(rechargeParam.getCoinCode())){
            rechargeParam.setCoinCode("USDT.TRC20");
        }

        rechargeParam.setNotifyUrl(rechargeNotifyUrl);
        rechargeParam.setSign(getSign(rechargeParam, secret_key));
        String req = HttpUtil.doPostJson(url + RECHARGE_URL, JsonUtil.toJson(rechargeParam));
        BiPayResp biPayResp = JsonUtil.fromJsonFastJSON(BiPayResp.class, req);
        if (biPayResp != null && 1000 == biPayResp.getCode()) {
            String data = biPayResp.getData();
            return JsonUtil.fromJsonFastJSON(RechargeResp.class, data);
        }
        return null;
    }


    /**
     * 三方回调  充值  购买vip
     *
     * @param rechargeNotify
     * @return
     */
    @Nullable
    public static RechargeNotify rechargeOrderNotify(RechargeNotify rechargeNotify, String secret_key) {
        try {
            String sign = getSign(rechargeNotify, secret_key);
            if (StringUtil.isNotEmpty(sign)) {
                if (sign.equals(rechargeNotify.getSign())) {
                    return rechargeNotify;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 创建提款单
     *
     * @param withdrawParam
     * @return
     */
    @Nullable
    public static WithdrawResp createWithdrawOrder(WithdrawParam withdrawParam, PaymentChannel paymentChannel) {

        withdrawParam.setNotifyUrl(HttpUtil.getBtServerName() + WITHDRAW_NOTIFY_URL);
        withdrawParam.setUserId(paymentChannel.getCurrencyCode());
        withdrawParam.setSign(getSign(withdrawParam, paymentChannel.getPrivateKey()));
        String req = HttpUtil.doPostJson(paymentChannel.getDzkey() + WITHDRAW_URL, JsonUtil.toJson(withdrawParam));
        log.error("三方返回结果:{}",req);
        BiPayResp biPayResp = JsonUtil.fromJsonFastJSON(BiPayResp.class, req);
        if (biPayResp != null && 1000 == biPayResp.getCode()) {
            String data = biPayResp.getData();
            if( StringUtil.isEmpty(data) ){
                throw new RuntimeException(String.format("[BiPay]三方返回结果: %s", req));
            }
            return JsonUtil.fromJsonFastJSON(WithdrawResp.class, data);
        }else {
            throw new RuntimeException(String.format("[BiPay]三方返回结果: %s", req));
        }
    }

    /**
     * 三方回调 提现
     *
     * @param withdrawNotify
     * @return
     */
    @Nullable
    public static WithdrawNotify withdrawOrderNotify(WithdrawNotify withdrawNotify, String secret_key) {
        try {
            String sign = getSign(withdrawNotify, secret_key);
            if (StringUtil.isNotEmpty(sign)) {
                if (sign.equals(withdrawNotify.getSign())) {
                    return withdrawNotify;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 签名 参数
     *
     * @param object
     * @return
     */
    private static String getSign(Object object, String secret_key) {
        Map<String, Object> dataMap = null;
        if (object instanceof RechargeParam) {
            RechargeParam rechargeParam = (RechargeParam) object;
            dataMap = BeanUtil.beanToMap(rechargeParam);
            dataMap.remove("customId");
            dataMap.remove("remarks");
        }
        if (object instanceof WithdrawParam) {
            WithdrawParam withdrawParam = (WithdrawParam) object;
            dataMap = BeanUtil.beanToMap(withdrawParam);
            dataMap.remove("instruction");
        }
        if (object instanceof RechargeNotify) {
            RechargeNotify rechargeNotify = (RechargeNotify) object;
            dataMap = BeanUtil.beanToMap(rechargeNotify);
            dataMap.remove("remarks");
            dataMap.remove("customId");
            dataMap.remove("sign");
        }
        if (object instanceof WithdrawNotify) {
            WithdrawNotify withdrawNotify = (WithdrawNotify) object;
            dataMap = BeanUtil.beanToMap(withdrawNotify);
            dataMap.remove("sign");
        }
        if (dataMap != null) {
            dataMap.remove("class");
            dataMap.remove("sign");
            return generateMD5Sign(dataMap, secret_key);
        }
        return null;
    }


    /**
     * 生成MD5签名 map中不能包含key sign
     */
    private static String generateMD5Sign(Map<String, Object> map, String secret) {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> list = new ArrayList<>(map.keySet());
        //调用sort方法并重写比较器进行升/降序
        list.sort((o1, o2) -> o1.compareTo(o2) > 0 ? 1 : -1);
        for (String k : list) {
            if (StringUtil.isNotEmpty(map.get(k) + "")) {
                stringBuilder.append(String.format("%s=%s&", k, map.get(k)));
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("&"));
        stringBuilder.append(secret);
        return MD5.md5(stringBuilder.toString(), "sign").toLowerCase();
    }

}
