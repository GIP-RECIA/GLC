/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.glc.security.cas;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.session.HashMapBackedSessionMappingStorage;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by jgribonvald on 01/06/16.
 */

/**
 * Performs CAS single sign-out operations in an API-agnostic fashion.
 *
 * @author Marvin S. Addison
 * @version $Revision$ $Date$
 * @since 3.1.12
 */
@Slf4j
public final class CustomSingleSignOutHandler {

  public static final String DEFAULT_ARTIFACT_PARAMETER_NAME = "ticket";
  public static final String DEFAULT_LOGOUT_PARAMETER_NAME = "logoutRequest";
  public static final String DEFAULT_FRONT_LOGOUT_PARAMETER_NAME = "SAMLRequest";
  public static final String DEFAULT_RELAY_STATE_PARAMETER_NAME = "RelayState";
  private static final int DECOMPRESSION_FACTOR = 10;

  /**
   * Mapping of token IDs and session IDs to HTTP sessions
   */
  private SessionMappingStorage sessionMappingStorage = new HashMapBackedSessionMappingStorage();

  /**
   * The name of the artifact parameter.  This is used to capture the session identifier.
   */
  private String artifactParameterName = DEFAULT_ARTIFACT_PARAMETER_NAME;

  /**
   * Parameter name that stores logout request for back channel SLO
   */
  private String logoutParameterName = DEFAULT_LOGOUT_PARAMETER_NAME;

  /**
   * Parameter name that stores logout request for front channel SLO
   */
  private String frontLogoutParameterName = DEFAULT_FRONT_LOGOUT_PARAMETER_NAME;

  /**
   * Parameter name that stores the state of the CAS server webflow for the callback
   */
  private String relayStateParameterName = DEFAULT_RELAY_STATE_PARAMETER_NAME;

  /**
   * The prefix url of the CAS server
   */
  private String casServerUrlPrefix = "";

  private boolean artifactParameterOverPost = false;

  private boolean eagerlyCreateSessions = true;

  private List<String> safeParameters;

  private final LogoutStrategy logoutStrategy = isServlet30() ? new Servlet30LogoutStrategy() : new Servlet25LogoutStrategy();

  public void setSessionMappingStorage(final SessionMappingStorage storage) {
    this.sessionMappingStorage = storage;
  }

  public void setArtifactParameterOverPost(final boolean artifactParameterOverPost) {
    this.artifactParameterOverPost = artifactParameterOverPost;
  }

  public SessionMappingStorage getSessionMappingStorage() {
    return this.sessionMappingStorage;
  }

  /**
   * @param name Name of the authentication token parameter.
   */
  public void setArtifactParameterName(final String name) {
    this.artifactParameterName = name;
  }

  /**
   * @param name Name of parameter containing CAS logout request message for back channel SLO.
   */
  public void setLogoutParameterName(final String name) {
    this.logoutParameterName = name;
  }

  /**
   * @param casServerUrlPrefix The prefix url of the CAS server.
   */
  public void setCasServerUrlPrefix(final String casServerUrlPrefix) {
    this.casServerUrlPrefix = casServerUrlPrefix;
  }

  /**
   * @param name Name of parameter containing CAS logout request message for front channel SLO.
   */
  public void setFrontLogoutParameterName(final String name) {
    this.frontLogoutParameterName = name;
  }

  /**
   * @param name Name of parameter containing the state of the CAS server webflow.
   */
  public void setRelayStateParameterName(final String name) {
    this.relayStateParameterName = name;
  }

  public void setEagerlyCreateSessions(final boolean eagerlyCreateSessions) {
    this.eagerlyCreateSessions = eagerlyCreateSessions;
  }

  /**
   * Initializes the component for use.
   */
  public synchronized void init() {
    if (this.safeParameters == null) {
      CommonUtils.assertNotNull(this.artifactParameterName, "artifactParameterName cannot be null.");
      CommonUtils.assertNotNull(this.logoutParameterName, "logoutParameterName cannot be null.");
      CommonUtils.assertNotNull(this.frontLogoutParameterName, "frontLogoutParameterName cannot be null.");
      CommonUtils.assertNotNull(this.sessionMappingStorage, "sessionMappingStorage cannot be null.");
      CommonUtils.assertNotNull(this.relayStateParameterName, "relayStateParameterName cannot be null.");
      CommonUtils.assertNotNull(this.casServerUrlPrefix, "casServerUrlPrefix cannot be null.");

      if (CommonUtils.isBlank(this.casServerUrlPrefix))
        log.warn("Front Channel single sign out redirects are disabled when the 'casServerUrlPrefix' value is not set.");

      this.safeParameters = this.artifactParameterOverPost
        ? Arrays.asList(this.logoutParameterName, this.artifactParameterName)
        : Collections.singletonList(this.logoutParameterName);
    }
  }

  /**
   * Determines whether the given request contains an authentication token.
   *
   * @param request HTTP reqest.
   * @return True if request contains authentication token, false otherwise.
   */
  boolean isTokenRequest(final HttpServletRequest request) {
    return CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.artifactParameterName, this.safeParameters));
  }

  /**
   * Determines whether the given request is a CAS back channel logout request.
   *
   * @param request HTTP request.
   * @return True if request is logout request, false otherwise.
   */
  private boolean isBackChannelLogoutRequest(final HttpServletRequest request) {
    return "POST".equals(request.getMethod())
      && !isMultipartRequest(request)
      && CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.logoutParameterName, this.safeParameters));
  }

  /**
   * Determines whether the given request is a CAS front channel logout request.  Front Channel log out requests are only supported
   * when the 'casServerUrlPrefix' value is set.
   *
   * @param request HTTP request.
   * @return True if request is logout request, false otherwise.
   */
  private boolean isFrontChannelLogoutRequest(final HttpServletRequest request) {
    return "GET".equals(request.getMethod()) && CommonUtils.isNotBlank(this.casServerUrlPrefix)
      && CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.frontLogoutParameterName));
  }

  /**
   * Process a request regarding the SLO process: record the session or destroy it.
   *
   * @param request  the incoming HTTP request.
   * @param response the HTTP response.
   * @return if the request should continue to be processed.
   */
  public boolean process(final HttpServletRequest request, final HttpServletResponse response) {
    if (isTokenRequest(request)) {
      log.trace("Received a token request");
      recordSession(request);
      return true;

    } else if (isBackChannelLogoutRequest(request)) {
      log.trace("Received a back channel logout request");
      destroySession(request);
      return false;

    } else if (isFrontChannelLogoutRequest(request)) {
      log.trace("Received a front channel logout request");
      destroySession(request);
      // redirection url to the CAS server
      final String redirectionUrl = computeRedirectionToServer(request);
      if (redirectionUrl != null) CommonUtils.sendRedirect(response, redirectionUrl);
      return false;

    } else {
      log.trace("Ignoring URI for logout: {}", request.getRequestURI());
      return true;
    }
  }

  /**
   * Associates a token request with the current HTTP session by recording the mapping
   * in the configured {@link SessionMappingStorage} container.
   *
   * @param request HTTP request containing an authentication token.
   */
  private void recordSession(final HttpServletRequest request) {
    final HttpSession session = request.getSession(this.eagerlyCreateSessions);

    if (session == null) {
      log.debug("No session currently exists (and none created). Cannot record session information for single sign out.");
      return;
    }

    final String token = CommonUtils.safeGetParameter(request, this.artifactParameterName, this.safeParameters);
    log.debug("Recording session for token {}", token);

    try {
      this.sessionMappingStorage.removeBySessionById(session.getId());
    } catch (final Exception e) {
      // ignore if the session is already marked as invalid.  Nothing we can do!
    }
    sessionMappingStorage.addSessionById(token, session);
  }

  /**
   * Uncompress a logout message (base64 + deflate).
   *
   * @param originalMessage the original logout message.
   * @return the uncompressed logout message.
   */
  private String uncompressLogoutMessage(final String originalMessage) {
    final byte[] binaryMessage = DatatypeConverter.parseBase64Binary(originalMessage);

    Inflater decompresser = null;
    try {
      // decompress the bytes
      decompresser = new Inflater();
      decompresser.setInput(binaryMessage);
      final byte[] result = new byte[binaryMessage.length * DECOMPRESSION_FACTOR];
      final int resultLength = decompresser.inflate(result);

      // decode the bytes into a String
      return new String(result, 0, resultLength, StandardCharsets.UTF_8);
    } catch (final Exception e) {
      log.error("Unable to decompress logout message", e);
      throw new RuntimeException(e);
    } finally {
      if (decompresser != null) decompresser.end();
    }
  }

  /**
   * Destroys the current HTTP session for the given CAS logout request.
   *
   * @param request HTTP request containing a CAS logout message.
   */
  private void destroySession(final HttpServletRequest request) {
    final String logoutMessage;
    // front channel logout -> the message needs to be base64 decoded + decompressed
    logoutMessage = isFrontChannelLogoutRequest(request)
      ? uncompressLogoutMessage(CommonUtils.safeGetParameter(request, this.frontLogoutParameterName))
      : CommonUtils.safeGetParameter(request, this.logoutParameterName, this.safeParameters);
    log.trace("Logout request:\n{}", logoutMessage);

    final String token = XmlUtils.getTextForElement(logoutMessage, "SessionIndex");
    if (CommonUtils.isNotBlank(token)) {
      final HttpSession session = this.sessionMappingStorage.removeSessionByMappingId(token);

      if (session != null) {
        String sessionID = session.getId();
        log.debug("Invalidating session [{}] for token [{}]", sessionID, token);

        try {
          session.invalidate();
        } catch (final IllegalStateException e) {
          log.debug("Error invalidating session.", e);
        }
        this.logoutStrategy.logout(request);
      }
    }
  }

  /**
   * Compute the redirection url to the CAS server when it's a front channel SLO
   * (depending on the relay state parameter).
   *
   * @param request The HTTP request.
   * @return the redirection url to the CAS server.
   */
  private String computeRedirectionToServer(final HttpServletRequest request) {
    final String relayStateValue = CommonUtils.safeGetParameter(request, this.relayStateParameterName);
    // if we have a state value -> redirect to the CAS server to continue the logout process
    if (StringUtils.isNotBlank(relayStateValue)) {
      final StringBuilder buffer = new StringBuilder().append(casServerUrlPrefix);

      if (!this.casServerUrlPrefix.endsWith("/")) buffer.append("/");
      buffer.append("logout?_eventId=next&")
        .append(this.relayStateParameterName)
        .append("=")
        .append(CommonUtils.urlEncode(relayStateValue));
      final String redirectUrl = buffer.toString();

      log.debug("Redirection url to the CAS server: {}", redirectUrl);
      return redirectUrl;
    }
    return null;
  }

  private boolean isMultipartRequest(final HttpServletRequest request) {
    return request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart");
  }

  private static boolean isServlet30() {
    try {
      return HttpServletRequest.class.getMethod("logout") != null;
    } catch (final NoSuchMethodException e) {
      return false;
    }
  }

  /**
   * Abstracts the ways we can force logout with the Servlet spec.
   */
  private interface LogoutStrategy {

    void logout(HttpServletRequest request);

  }

  private class Servlet25LogoutStrategy implements LogoutStrategy {

    public void logout(final HttpServletRequest request) {
      // nothing additional to do here
    }

  }

  private class Servlet30LogoutStrategy implements LogoutStrategy {

    public void logout(final HttpServletRequest request) {
      try {
        request.logout();
      } catch (final ServletException e) {
        log.debug("Error performing request.logout.");
      }
    }

  }

}
