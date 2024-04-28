package cn.rh.flash.sdk.paymentChannel.Mpay.util;

import java.security.MessageDigest;

public class MD5Utils {
    private static final char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    /**
     * 加密
     * @param pwd
     * @return
     */
    public static String hash(String pwd){

        if (pwd == null) {
            return null;
        }
        try {
            byte[] btInput = pwd.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 验证
     * @param oldPwd
     * @param hash
     * @return
     */
    public static boolean match(String str , String hash){
        if (hash == null || str == null) {
            return false;
        }
        return hash.equals(hash(str));
    }

}
