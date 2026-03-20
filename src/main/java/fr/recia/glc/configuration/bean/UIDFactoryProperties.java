package fr.recia.glc.configuration.bean;

import lombok.Data;

import java.util.Map;

@Data
public class UIDFactoryProperties {
    private String defaultCodeGenerateur;
    private Map<String,String> domainToCodeGenerateur;
}
