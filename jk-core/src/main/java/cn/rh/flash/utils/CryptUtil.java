package cn.rh.flash.utils;


import lombok.extern.log4j.Log4j2;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密工具类
*/
@Log4j2
public class CryptUtil {

/*    public static void main(String[] args) throws Exception {
        String test1 = "jk";
        String test = new String(test1.getBytes(), "UTF-8");

        String jiami = encrypt(test);
        System.out.println("数据：" + test);
        System.out.println("加密：" + jiami); //G6PUtTzzZovvx3/yYJ1wTg==

        String jiemi = desEncrypt("G6PUtTzzZovvx3/yYJ1wTg==").trim();
        System.out.println("解密：" + jiemi); // jk
    }*/
    //使用AES-128-CBC加密模式，key需要为16位,
    public static final String KEY = "jk007.!!!!!!!!16";
    private static String IV = "1234567890123456";

    public static String encodeBASE64(byte[] bytes) {
        String encode = Base64.getEncoder().encodeToString(bytes);
        encode = encode.replaceAll("\n", "");
        return encode;
    }


    /**
     * 加密方法
    * @param data 要加密的数据
     * @param key  加密key
     * @param iv   加密iv
     * @return 加密的结果
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"算法/模式/补码方式"NoPadding PkcsPadding
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密方法
    * @param data 要解密的数据
     * @param key  解密key
     * @param iv   解密iv
     * @return 解密的结果
     * @throws Exception
     */
    public static String desEncrypt(String data, String key, String iv) throws Exception {

        byte[] encrypted1 = Base64.getDecoder().decode(data);

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original, StandardCharsets.UTF_8);
        return originalString.trim();

    }

    /**
     * 使用默认的key和iv加密
    * @param data
     * @return
     * @throws Exception
     */
    public static String encrypt(String data) {
        try {
            return encrypt(data, KEY, IV);
        } catch (Exception e) {
            throw new RuntimeException("加密失败"+e);
        }
    }

    /**
     * 使用默认的key和iv解密
    * @param data
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String data){
        try {
            return desEncrypt(data, KEY, IV);
        } catch (Exception e) {
            log.error("解密失败:"+e.getMessage());
            throw new RuntimeException("Decryption failed!");
        }
    }

}
