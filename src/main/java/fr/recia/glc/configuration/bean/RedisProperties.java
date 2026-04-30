package fr.recia.glc.configuration.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "redis")
@Data
@Validated
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class RedisProperties {

    private String hostName;
    private int port;
    private String userName;
    private String password;
    private int databaseIndex;
    private String mappingPrefix;
    private String indexPrefix;

    @PostConstruct
    public void setupAndDebug() {
        if(databaseIndex < 0){
            databaseIndex = 0;
            log.warn("Negative database index provided, defaulted to 0");
        }
        log.info("RedisProperties {}", this);
    }

    @Override
    public String toString() {
        return "RedisProperties{" +
            "hostName='" + hostName + '\'' +
            ", port=" + port +
            ", userName='" + userName + '\'' +
            ", password='" + password + '\'' +
            ", databaseIndex=" + databaseIndex +
            ", mappingPrefix='" + mappingPrefix + '\'' +
            ", indexPrefix='" + indexPrefix + '\'' +
            '}';
    }
}
