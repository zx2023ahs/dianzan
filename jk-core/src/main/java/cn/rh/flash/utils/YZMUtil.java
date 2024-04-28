package cn.rh.flash.utils;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.alibaba.fastjson.JSONObject;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class YZMUtil {
    private static final Random random = new Random();
    /**
     * 生成验证码图片
     * @param request 设置session
     * @param response 转成图片
     * @param captchaProducer 生成图片方法类
     * @param validateSessionKey session名称
     * @throws Exception
     */
    public static void validateCode(HttpServletRequest request, HttpServletResponse response, DefaultKaptcha captchaProducer, String validateSessionKey) throws Exception{
        // Set to expire far in the past.
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
 
        // return a jpeg
        response.setContentType("image/jpeg");
 
        // create the text for the image
        String capText = captchaProducer.createText();
 
        // store the text in the session
        request.getSession().setAttribute(validateSessionKey, capText);
 
        // create the image with the text
        BufferedImage bi = captchaProducer.createImage(capText);
 
        ServletOutputStream out = response.getOutputStream();
 
        // write the data out
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }



    /**
     * 生成验证码图片
     * @return
     */
    public static final String KEY_CODE = "key_code_jk";
    public static final String KEY_IMG = "key_img_jk";

    public static Map<String,String> getYZMCode(){
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100,4,0);
        ;
        Map<String,String> map =  new HashMap<>();
        map.put(KEY_IMG,lineCaptcha.getImageBase64Data() );
        map.put(KEY_CODE,lineCaptcha.getCode());
        return map;
    }




    /**
     * 生成验证码图片
     * @throws Exception
     * @param captchaProducer
     */
    public static String getYZMCode(DefaultKaptcha captchaProducer,String yzm){
        // create the text for the image

        //生成图片
        BufferedImage bufferedImage = captchaProducer.createImage("3333");
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(outputStream.toByteArray());
            return "data:image/jpeg;base64," + base64.replaceAll("\r\n", "");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }


    /**
     * 生成运算式
     * @return
     */
    public static JSONObject createEquation() {

        // 定义数组存放加减二个运算符
        char[] arr = {'+', '-'};

        // 生成10以内的随机整数num1
        int num1 = random.nextInt(30);

        // 生成一个0-2之间的随机整数operate
        int operate = random.nextInt(2);

        // 生成10以内的随机整数num1
        int num2 = random.nextInt(30);

        // 避免出现除数为0的情况
        if (num1 < num2) {
            int temp = num1;
            num1 = num2;
            num2 = temp;
        }

        // 运算结果
        int captchaNo = 0;

        // 假定position值0/1分别代表”+”,”-”，计算前面操作数的运算结果
        switch (operate) {
            case 0:
                captchaNo = num1 + num2;
                break;
            case 1:
                captchaNo = num1 - num2;
                break;
        }

        // 将生成的验证码值(即运算结果的值)放到session中，以便于后台做验证。
//        HttpSession session = request.getSession();
//        session.setAttribute("captchaNo", captchaNo);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question",num1+" "+ arr[operate]+" "+num2+" = ?");
        jsonObject.put("answer",captchaNo);
        return jsonObject;
//        return num1+" "+ arr[operate]+" "+num2+" = ?";
    }

    /**
     * 生成运算式
     * @return
     */
    public static JSONObject createCode() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question",RandomUtil.getRandomYZMString(4));
        return jsonObject;
    }

}