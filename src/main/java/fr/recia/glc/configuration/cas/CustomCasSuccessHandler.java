package fr.recia.glc.configuration.cas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handler simplifié pour le succès d'authentification CAS.
 * Répond en JSON pour les appels API, sinon redirige l'utilisateur.
 */
@Slf4j
@Component
public class CustomCasSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private CustomSessionMappingStorage redisService;

    @PostConstruct
    void init(){
        this.setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Authentification terminé avec succés Utilisateur authentifié : {}", authentication.getName());

        // URI et type de requête
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");

        log.debug("URI de la requête : {}", uri);
        log.debug("Header Accept : {}", accept != null ? accept : "null");

        // Log tous les headers de la requête
        log.trace("===== Headers de la requête [CAS SH] =====");
        var headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                log.trace("[CAS SH] {}: {}", headerName, headerValue);
            }
        }
        log.trace("===== Fin des headers de la requête [CAS SH] =====");

        // Log des headers de réponse
        log.trace("===== Headers de la réponse [CAS SH] =====");
        for (String headerName : response.getHeaderNames()) {
            String headerValue = response.getHeader(headerName);
            log.trace("[CAS SH] {}: {}", headerName, headerValue);
        }
        log.trace("===== Fin des headers de la réponse [CAS SH] =====");

        // Authentification et session
        log.debug("Utilisateur authentifié : {}", authentication.getName());
        String credentials = (String) authentication.getCredentials();
        log.debug("Credentials (Session Ticket) : {}", credentials);
        String sessionId = request.getSession(false).getId();
        log.debug("Session ID : {}", sessionId);

        if (credentials == null) {
            throw new RuntimeException("Ticket perdu pour la session: " + sessionId);
        }
        log.debug("Création du mappage entre le ticket [{}] et l'ID de session [{}] dans le cache Redis", credentials, sessionId);
        redisService.setSessionTicketSessionIdPair(credentials, sessionId);

        super.onAuthenticationSuccess(request, response, authentication);
    }



}
