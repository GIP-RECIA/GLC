package fr.recia.glc.configuration.cas;

import fr.recia.glc.configuration.GLCProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Classe custom qui permet de gérer le multidomaine lors de la demande d'émission d'un ST
 */
@Slf4j
public class CustomCasAuthenticationEntryPoint extends CasAuthenticationEntryPoint {

    @Autowired
    private GLCProperties glcProperties;

    @Override
    protected String createServiceUrl(HttpServletRequest request, HttpServletResponse response) {
        final String url = request.getRequestURL().toString();
        final String uri = request.getRequestURI();
        return url.substring(0, url.length() - uri.length()) + glcProperties.getCas().getCasServiceId();
    }

}
