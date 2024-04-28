package cn.rh.flash.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA签名工具类
 * 通过SHA1withRSA算法，私钥加密，公钥验签。
 * 密钥长度选择1024bit，密钥格式选择PKCS#1
 */
public class RSAUtils {

    /**
     * 私钥生成签名
     * @param data 签名原串
     * @param privateKeyString 商户私钥
     * @return
     */
    public static String encrypt(String data,String privateKeyString) throws Exception{
        // 私钥转换为字节数组
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        // 转换为私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        // 使用私钥对数据进行签名，算法选择 SHA1withRSA
        byte[] bytes = data.getBytes("UTF-8");

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        signature.update(bytes);
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }


    /**
     * 公钥验证签名
     * @param data 返回参数原串
     * @param sign 返回签名
     * @param publicKeyString 平台公钥
     * @return
     * @throws Exception
     */
    public static Boolean verify(String data,String sign,String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        // 转换为公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        // 要验签的数据
        byte[] dataBytes = data.getBytes("UTF-8");

        // 将签名字符串转换为字节数组
        byte[] signatureBytes = Base64.getDecoder().decode(sign);

        // 使用公钥进行验签，算法选择 SHA1withRSA
        Signature signatureVerifier = Signature.getInstance("SHA1withRSA");
        signatureVerifier.initVerify(publicKey);
        signatureVerifier.update(dataBytes);

        // 验证签名
        return  signatureVerifier.verify(signatureBytes);
    }

//    public static void main(String[] args) throws Exception{
//        String data="amount=20&bankCode=ALIPAY&merchantId=10383&merchantOrderNo=test2024020100002&model=813&notifyUrl=http://47.242.0.145:8082/test/test2&version=1.0.0";
////        String data="amount=200&bankCode=DIGITWALLET&merchantId=10383&merchantOrderNo=test2024020100004&model=901&notifyUrl=http://47.242.0.145:8082/test/test2&version=1.0.0";
//
//        String prkey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIXXQXVxuTUC+htL5jAOyiTK6srz7cxcJduXPpP6kbcNbrp3s1bMjMixmtR1T3iD2b5PIyasWGsYDYZ7Qh1ASRNmiQxx7n3k08O2Joxmz8L1Y0It+Aovep7hDr7rINGnENJ/oc4BtOfBvd9I/jYjLDAvl/vPlrr/8kHrx5Hw4Yp1AgMBAAECgYBbFswDHVn6XUNQ7pAEJxyme7/eTrOKATD2yXGajs87MRYqLCtPKLmsIUwJyqlCkSHRZtS6jlNC18TkwOCXPdg9Mrmrk5B5ccsnMTSNDdBkoD/S29roSxKGjn8M8kQVtxn57NM7jGz+hIIJChFhaLJ6zsclAOawePVF35xNghGH4QJBAMFfjpYVSUkoX7l2HoVI6SJjBuobwr2hql30kgO5qd4d46xlU0fRxOzAS9eH0PtG48qKC3DEkseTAZzjlbyA+hkCQQCxL+gc/eR6H9Egt7eIwD/UP+HYWtLBKzLq94jrDCBtAmC4rbZbItVonpEDygvEnqo5FwgXcirLJQfKr3NcWNa9AkEAndYWd1B2mh03TRUpwlcJ3ASCX1I7eTdc7QW0rQ+9pEw3Sr8F5AhHuYsYHJuCK6foRKi6v9fjirzmQx3MWanY+QJBAIjIw/aFpfkFUOtiCGSgjWXsPwxH6QVItREhMOjLPskImeRX7jvi4z7VknuLYZRTLHPQLOX0s8aJer9kvLjguD0CQDW30LF6ONroa11b5/TbDd/4et3OKlAwxkhnqnbi6X2vCxOs7RbzNaTQ9iXA/+yu4h8y1qFru0xvZn+larZ/8LM=";
//
//        System.out.println(RSAUtils.encrypt(data, prkey));
//
//        //---
//
//        String returnData="amount=200&code=0&merchantOrderNo=test2024020100004&msg=操作成功&url=https://myapi.baidutestpay.com/api/payOrder/jump/202402011428352613520";
//
//        String puKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCl9bPaX3zFMnAGCHjv6SayaCJjWJpAj+Gbrdltog2ocb2+/Kl7vX5wSao5pbAh0WMYM6S9bcog+shg4H09juXOLkacXPoiSXYciU+jbrEYEx+5bJqVeuMKVu4cRwlV5wvcMPDbeLWhXaGCRc3yZhPJ5yGGUyaUKCe138xjGvJYnwIDAQAB";
//
//        String sign="duK/SxKWxo6kPxOnwUKAY4nZ6HrMgD9z2vt3aUR9rqRN9eTFIS+uEtoopr1Vf7U+vsGXrd6YAwc9bktavibs0M0l+09a6lW8O1DZC37lf3IbbTLGx9+Mxb7P4t4E7o8HBPMuMOZB+wocLHVMlp2OjgL4pvLLnx7YbDy5q4Ss7Ek=";
//
//        System.out.println(RSAUtils.verify(returnData, sign, puKey));
//    }

}
