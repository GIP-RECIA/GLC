package fr.recia.glc.configuration;

import fr.recia.redis.session.cleanup.model.RedisCleanupConfig;
import fr.recia.redis.session.cleanup.service.RedisSessionCleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class RedisSessionCleanupConfiguration {

    @Autowired
    private GLCProperties glcProperties;

    @Autowired
    @Qualifier("customRedisTemplate")
    private RedisTemplate<String, Object> customRedisTemplate;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("redis-session-clean-glc-");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }


    @Bean
    public RedisCleanupConfig redisCleanupConfig() throws UnknownHostException {
        return new RedisCleanupConfig(glcProperties.getRedis().getIndexPrefix(), InetAddress.getLocalHost().getHostName().equals(glcProperties.getSession().getCronAllowedHostname()), glcProperties.getSession().getCronExpression());
    }

    @Bean
    public RedisSessionCleanupService redisSessionCleanupService(FindByIndexNameSessionRepository<? extends Session> sessionRepository, RedisCleanupConfig config, TaskScheduler taskScheduler) {
        return new RedisSessionCleanupService(customRedisTemplate, sessionRepository, config, taskScheduler );
    }
}
