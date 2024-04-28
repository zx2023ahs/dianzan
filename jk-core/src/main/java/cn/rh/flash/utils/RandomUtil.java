package cn.rh.flash.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;


public class RandomUtil {



    /**
     * 图片验证码
     */
    public static String getRandomYZMString(int length) {
        String base = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    /**
     * 获取随机位数的字符串  不包含 d
     */
    public static String getRandomString(int length) {
        String base = "abcefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomYZMNumber(int length) {
        String base = "1234567890";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 随机名称
     * @param length
     * @return
     */
    public static String getRandomName(int length) {
        String base = "abcefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /* 邀请码 */
    public static String getrandomInvitationCode() {
        return String.format("d%s", RandomUtil.getRandomString(5) );
    }

    /**
     * 范围取随机数
     * @param min
     * @param max
     * @return
     */
    public static double randomByMaxAndMin( double min,  double max){
        Random random = new Random();
        double randomNumber = min + (max - min) * random.nextDouble();
        return new BigDecimal(randomNumber).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
    }


}
