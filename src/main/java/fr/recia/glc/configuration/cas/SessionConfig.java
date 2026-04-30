package fr.recia.glc.configuration.cas;

import fr.recia.glc.configuration.GLCProperties;
import fr.recia.glc.configuration.bean.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Slf4j
@Configuration
@EnableRedisHttpSession(redisNamespace = "${spring.redis.namespace}")
public class SessionConfig {

    @Autowired
    private GLCProperties glcProperties;

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        log.info("Load LettuceConnectionFactory");
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(glcProperties.getRedis().getHostName());
        config.setPort(glcProperties.getRedis().getPort());
        config.setPassword(glcProperties.getRedis().getPassword());
        config.setUsername(glcProperties.getRedis().getUserName());
        config.setDatabase(glcProperties.getRedis().getDatabaseIndex());
        return new LettuceConnectionFactory(config);
    }

}
