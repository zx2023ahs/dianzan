package cn.rh.flash.api.config;

import cn.rh.flash.api.interceptor.MyApi2Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;


@Configuration
public class ApiWebMvc implements WebMvcConfigurer {

    //@Autowired
    //private MyApiInterceptor myApiInterceptor;

    @Autowired
    private MyApi2Interceptor myApi2Interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myApi2Interceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/content/**",
                        "/api/auth/login_v1",
//                        "/api/auth/reg_v1",
                        "/api/auth/reg_v2",
                        "/api/auth/getValidateCode",
                        "/api/auth/getValidateCodeV2",
                        "/api/auth/isSendPhoneCode",
                        "/api/auth/getH5DefLang",

                        "/api/pay/notifyRechargeOrder",
                        "/api/pay/notifyRechargeOrderVIP",
                        "/api/pay/notifyWithdrawOrder",
//                        "/api/pay/notifyHandler",
//                        "/api/pay/notifyVipHandler",
//                        "/api/pay/testVip",
//                        "/api/pay/testOrder",

                        "/api/pay/wallet/notifyRechargeOrder",
                        "/api/pay/wallet/notifyRechargeOrderVIP",
                        "/api/pay/wallet/notifyWithdrawOrder",

                        "/api/pay/kdpay/notifyRechargeOrder",
                        "/api/pay/kdpay/notifyRechargeOrderVIP",
                        "/api/pay/kdpay/notifyWithdrawOrder",

                        "/api/pay/cbpay/notifyRechargeOrder",
                        "/api/pay/cbpay/notifyRechargeOrderVIP",
                        "/api/pay/cbpay/notifyWithdrawOrder",

                        "/api/pay/mpay/notifyRechargeOrder",
                        "/api/pay/mpay/notifyRechargeOrderVIP",
                        "/api/pay/mpay/notifyWithdrawOrder",

                        "/api/pay/qnqbpay/notifyRechargeOrder",
                        "/api/pay/qnqbpay/notifyRechargeOrderVIP",
                        "/api/pay/qnqbpay/notifyWithdrawOrder",

                        "/api/pay/mypay/notifyRechargeOrder",
                        "/api/pay/mypay/notifyRechargeOrderVIP",
                        "/api/pay/mypay/notifyWithdrawOrder",

                        "/api/pay/okpay/notifyRechargeOrder",
                        "/api/pay/okpay/notifyRechargeOrderVIP",
                        "/api/pay/okpay/notifyWithdrawOrder",

                        "/api/pay/fpay/notifyRechargeOrder",
                        "/api/pay/fpay/notifyRechargeOrderVIP",
                        "/api/pay/fpay/notifyWithdrawOrder",

                        "/api/pay/jdpay/notifyRechargeOrder",
                        "/api/pay/jdpay/notifyRechargeOrderVIP",
                        "/api/pay/jdpay/notifyWithdrawOrder",

                        "/api/pay/808pay/notifyRechargeOrder",
                        "/api/pay/808pay/notifyWithdrawOrder",

//                        "/api/auth/sendPhoneCode",
                        "/api/auth/sendPhoneCodeV2",
//                        "/api/auth/getValidateCode",
                        "/api/auth/forgetPassword",

                        //积分签到
//                        "/api/score/signRule",
//                        "/api/score/prizeList",

                        "/api/pb/getPowerBank",

                        "/swagger-ui/**",
                        "/v3/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/images/**",
                        "/configuration/security",
                        "/configuration/ui",
                        "/file/download",
                        "/file/getImgStream",
                        "/file/getImgBase64",
                        "/doc.html"
                );

    }

    // --------------------------------------------------------------------------
    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    /**
     * 增加字符串转日期的功能
     */
    @PostConstruct
    public void initEditableAvlidation() {

        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer) handlerAdapter.getWebBindingInitializer();
        if (initializer.getConversionService() != null) {
            GenericConversionService genericConversionService = (GenericConversionService) initializer.getConversionService();
            genericConversionService.addConverter(new StringToDateConverter());
        }
    }

    /* 日期转换 */
    private class StringToDateConverter implements Converter<String, Date> {
        private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
        private static final String shortDateFormat = "yyyy-MM-dd";

        @Override
        public Date convert(String value) {

            if (StringUtils.isEmpty(value)) {
                return null;
            }
            value = value.trim();

            try {
                if (value.contains("-")) {
                    SimpleDateFormat formatter;
                    if (value.contains(":")) {
                        formatter = new SimpleDateFormat(dateFormat);
                    } else {
                        formatter = new SimpleDateFormat(shortDateFormat);
                    }

                    Date dtDate = formatter.parse(value);
                    return dtDate;
                } else if (value.matches("^\\d+$")) {
                    Long lDate = new Long(value);
                    return new Date(lDate);
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("parser %s to Date fail", value));
            }
            throw new RuntimeException(String.format("parser %s to Date fail", value));
        }
    }


}
