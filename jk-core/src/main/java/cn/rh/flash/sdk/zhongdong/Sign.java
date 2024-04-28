package cn.rh.flash.sdk.zhongdong;

import lombok.extern.log4j.Log4j2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Log4j2
public class Sign {

    private static final String encodingCharset = "UTF-8";

    /**
     *
     * @return
     */
    public static String getSignValue(String sign) {
        String result = md5( sign );
        log.info("sign:{}", result);
        return result;
    }


    /**
     * <p><b>Description: </b>MD5
     * <p>2018年9月30日 上午11:33:19
     *
     * @param value
     * @return
     */
    public static String md5(String value) {
        MessageDigest md = null;
        try {
            byte[] data = value.getBytes();
            md = MessageDigest.getInstance("MD5");
            byte[] digestData = md.digest(data);
            return toHex(digestData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toHex(byte[] input) {
        if (input == null) {
            return null;
        }
        StringBuffer output = new StringBuffer(input.length * 2);
        for (int i = 0; i < input.length; i++) {
            int current = input[i] & 0xff;
            if (current < 16) {
                output.append("0");
            }
            output.append(Integer.toString(current, 16));
        }

        return output.toString();
    }
}
