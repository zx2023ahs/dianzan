package cn.rh.flash.api;

import cn.rh.flash.dao.BaseRepositoryFactoryBean;
import cn.rh.flash.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;

/**
 * ApiApplication
 */
@EnableCaching
@SpringBootApplication(
        //集成activit后，默认引入了springSecurity，这里需要通过下面配置去掉SpringSecurity
        exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class  }
)
@ComponentScan(basePackages = "cn.rh.flash")
@EntityScan(basePackages = "cn.rh.flash.bean.entity")
@EnableJpaRepositories(basePackages = "cn.rh.flash.dao", repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class, enableDefaultTransactions = false)
@EnableJpaAuditing
@EnableOpenApi
public class ApiApplication extends SpringBootServletInitializer {
    private static Logger logger = LoggerFactory.getLogger(ApiApplication.class);
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApiApplication.class);
    }

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(ApiApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String active = env.getProperty("spring.profiles.active");
        String port = env.getProperty("server.port");
        port = port == null ? "8080" : port;
        String path = env.getProperty("server.servlet.context-path");
        path = path == null ? "" : path;
        logger.info("\n----------------------------------------------------------\n\t" +
                "web is running! \n\t" +
                "系统运行环境 : \t" +active + "\n\t" +
                "本地访问地址 : \thttp://localhost:" + port + path + "/\n\t" +
                "外部访问地址 : \thttp://" + ip + ":" + port + path + "/\n\t" +
                "在线文档地址 : \thttp://" + ip + ":" + port + path + "/doc.html\n" +
                "----------------------------------------------------------");


        String timeZone = env.getProperty("spring.jackson.time-zone");
        TimeZone.setDefault( TimeZone.getTimeZone(timeZone));
        logger.info("\n----------------------------------------------------------\n\t" +
                "当前时区 : \t" +TimeZone.getDefault() + "\n\t" +
                "----------------------------------------------------------");
        logger.info("--------当前星期为:{}--------",DateUtil.getWeek(DateUtil.parse(DateUtil.getTime(), "yyyy-MM-dd HH:mm:ss")));
        //  测试获取时间
        logger.info("------------" + DateUtil.getTime() );
        logger.info("------------" + DateUtil.parseTime( DateUtil.getTime() ) );
    }
}
