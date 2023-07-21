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
package fr.recia.glc.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.glc.security.cas.CustomUserDetails;
import fr.recia.glc.security.cas.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@Slf4j
public class AccountController {

  @GetMapping(value = "/api/account")
  public ResponseEntity<CustomUserDetails> getAccount() {
    CustomUserDetails userDetails = SecurityUtils.getCurrentUserDetails();
    if (userDetails == null)
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    log.debug("REST request to get UserDetails account: {}", userDetails);

    return new ResponseEntity<>(userDetails, HttpStatus.OK);
  }

  @RequestMapping(value = "/app/login")
  public Object login(Model model, HttpServletRequest request) throws IOException {
    log.debug("=========> Login Call ");

        /*String then = request.getParameter("then");
        if (then != null) {
            log.debug("Going on redirect");
            // then = URLDecoder.decode(then, "UTF-8");
            String url = then;
            log.debug("url : {}", url);
            return "redirect:" + URI.create(url);
        }*/

    // retrieve the user
    CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
    if (user == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    log.debug("UserDetails {}", user);
    //log.debug("#########   RedirectURL is present ? {}", getRedirectUrl(request));
    String uri = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    log.debug("#########   Requested url {}", uri);

    String jsUser = new ObjectMapper().writeValueAsString(user);
    String content, type;
    if (request.getParameter("postMessage") != null) {
      log.debug("Going on postMessage");
      type = "text/html";
      content = "Login success, please wait...\n<script>\n (window.opener ? (window.opener.postMessage ? window.opener : window.opener.document) : window.parent).postMessage('loggedUser=' + JSON.stringify("
        + jsUser + "), '*');\n</script>";
    } else if (request.getParameter("callback") != null) {
      log.debug("Going on callback");
      type = "application/x-javascript";
      content = request.getParameter("callback") + "(" + jsUser + ")";
    } else {
      log.debug("Going on else");
      type = "application/json";
      content = jsUser;
    }
    log.debug("content : {}", content);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setContentType(MediaType.valueOf(type));

    return new ResponseEntity<>(content, responseHeaders, HttpStatus.OK);
  }

}
