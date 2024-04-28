package cn.rh.flash.api.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:application.yml")//读取application.yml文件
public class MyRedissonConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.jedis.pool.max-active}")
    private int max_active;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int max_idle;


    /**
     * Redisson 配置  单点模式
     * @return
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer().setAddress("redis://" + host + ":" + port)
                .setTimeout(timeout)
                .setDatabase( database )
                .setConnectionPoolSize( max_active )
                .setConnectionMinimumIdleSize( max_idle );
        if (StringUtils.isNotEmpty( password ) ) {
            serverConfig.setPassword( password );
        }
        return Redisson.create(config);
    }

}
