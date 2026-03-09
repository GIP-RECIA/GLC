package fr.recia.glc.configuration.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "restriction-rentree")
@Data
public class RestrictionRentreeProperties {
    private String url;
}
