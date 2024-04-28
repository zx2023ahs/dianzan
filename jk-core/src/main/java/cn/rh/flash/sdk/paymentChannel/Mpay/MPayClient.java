package cn.rh.flash.sdk.paymentChannel.Mpay;

import cn.rh.flash.sdk.paymentChannel.Mpay.util.HttpUtil;
import cn.rh.flash.sdk.paymentChannel.Mpay.util.RSAUtils;
import cn.rh.flash.sdk.paymentChannel.Mpay.util.MD5Utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.rh.flash.sdk.paymentChannel.Mpay.dao.*;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MPayClient {
    private final String apiUrl;

    private final int merchId;

    private final String md5Key;

    private final String publicRsaKey;

    /**
     * 绑定用户钱包地址
     *
     * @param username 用户名
     * @return
     */
    public BindResult bindUserAddress(String username) {
        try {
            Map<String, Object> bindMap = new HashMap<String, Object>() {{
                put("merchUserName", username);
            }};
            String url = this.apiUrl + "/api/v2/merch/user/bind" + makeRequestData(bindMap);
            String result = HttpUtil.sendHttpRequest(url);

            RequestResult resultStruct = JSON.parseObject(result, RequestResult.class);
            return new BindResult(resultStruct.code == 0, resultStruct.errMessage, resultStruct.data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new BindResult(false, e.getMessage(), "");
        }
    }

    /**
     * 用户上发单
     *
     * @param orderId 商户系统内订单号
     * @param amount  金额
     * @return
     */
    public Up userUp(String orderId, float amount,String callBackUrl) {
        try {
            Map<String, Object> bindMap = new HashMap<String, Object>() {{
                put("merchOrderId", orderId);
                put("amount", amount);
                put("callBackUrl", callBackUrl);
            }};
            String url = this.apiUrl + "/api/v2/merch/user/up" + makeRequestData(bindMap);
            String result = HttpUtil.sendHttpRequest(url);
            UpRequestResult resultStruct = JSON.parseObject(result, UpRequestResult.class);
            return new Up(resultStruct.code == 0, resultStruct.errMessage, resultStruct.data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new Up(false, e.getMessage(), null);
        }
    }

    /**
     * 用户下发单
     *
     * @param orderId           商户系统内订单号
     * @param amount            金额
     * @param userWalletAddress 用户钱包地址
     */
    public Down userDown(String orderId, float amount, String userWalletAddress,String remark,String callBackUrl) {
        try {
            Map<String, Object> bindMap = new HashMap<String, Object>() {{
                put("merchOrderId", orderId);
                put("amount", amount);
                put("userWalletAddress", userWalletAddress);
                put("remark", remark);
                put("callBackUrl", callBackUrl);
            }};
            String url = this.apiUrl + "/api/v2/merch/user/down" + makeRequestData(bindMap);
            String result = HttpUtil.sendHttpRequest(url);
            DownRequestResult resultStruct = JSON.parseObject(result, DownRequestResult.class);
            return new Down(resultStruct.code == 0, resultStruct.errMessage, resultStruct.data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new Down(false, e.getMessage(), null);
        }
    }

    /**
     * 查询商户上发单信息
     * @param orderId 商户系统内订单号
     */
    public MerchOrder getUpOrderInfo(String orderId) {
        try {
            Map<String, Object> bindMap = new HashMap<String, Object>() {{
                put("merchOrderId", orderId);
            }};
            String url = this.apiUrl + "/api/v2/merch/user/checkUpOrder" + makeRequestData(bindMap);
            String result = HttpUtil.sendHttpRequest(url);
            MerchOrderResult resultStruct = JSON.parseObject(result, MerchOrderResult.class);
            return new MerchOrder(resultStruct.code == 0, resultStruct.errMessage, resultStruct.data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new MerchOrder(false, e.getMessage(), null);
        }
    }

    /**
     * 查询商户下发单信息
     * @param orderId 商户系统内订单号
     */
    public MerchOrder getDownOrderInfo(String orderId) {
        try {
            Map<String, Object> bindMap = new HashMap<String, Object>() {{
                put("merchOrderId", orderId);
            }};
            String url = this.apiUrl + "/api/v2/merch/user/checkDownOrder" + makeRequestData(bindMap);
            String result = HttpUtil.sendHttpRequest(url);
            MerchOrderResult resultStruct = JSON.parseObject(result, MerchOrderResult.class);
            return new MerchOrder(resultStruct.code == 0, resultStruct.errMessage, resultStruct.data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new MerchOrder(false, e.getMessage(), null);
        }
    }


    private MPayClient(String apiUrl, String md5Key, String publicRsaKey, int merchId) {
        this.apiUrl = apiUrl;
        this.md5Key = md5Key;
        this.publicRsaKey = publicRsaKey;
        this.merchId = merchId;
    }

    public static MPayClient getInstance(String apiUrl, String md5Key, String publicRsaKey, int merchId) {
        Base64.Decoder base64 = Base64.getDecoder();
        return new MPayClient(apiUrl, md5Key, new String(base64.decode(publicRsaKey), StandardCharsets.UTF_8), merchId);
    }

    public String makeRequestData(Map<String, Object> bodyData) throws Exception {
        long date = System.currentTimeMillis() / 1000;
        String bodyString = JSONObject.toJSONString(bodyData);
        System.out.println(bodyString);
        PublicKey key = RSAUtils.getPublicKey(this.publicRsaKey);
        byte[] encrypted = RSAUtils.encrypt(bodyString.getBytes(StandardCharsets.UTF_8), key);
        String encryptedByte = Base64.getUrlEncoder().encodeToString(encrypted);
        String keyString = MD5Utils.hash(this.merchId + encryptedByte + date + this.md5Key);
        System.out.println("encryptedByte"+encryptedByte);
        System.out.println("keyString"+keyString);
        System.out.println("date"+date);
        return "?merchId=" + this.merchId + "&body=" + encryptedByte + "&t=" + date + "&key=" + keyString;
    }


}
