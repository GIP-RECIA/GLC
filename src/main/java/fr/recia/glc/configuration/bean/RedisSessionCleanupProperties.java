package fr.recia.glc.configuration.bean;

import lombok.Data;

@Data
public class RedisSessionCleanupProperties {
    String cronAllowedHostname;
    String cronExpression;
}
