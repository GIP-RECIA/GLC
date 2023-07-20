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

import fr.recia.glc.security.cas.CustomUserDetails;
import fr.recia.glc.security.cas.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/account")
@Slf4j
public class AccountController {

  @GetMapping(value = "/signin")
  public ResponseEntity<CustomUserDetails> signIn() {
    CustomUserDetails userDetails = SecurityUtils.getCurrentUserDetails();
    log.debug("UserDetails: {}", userDetails);
    if (userDetails == null)
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    return new ResponseEntity<>(userDetails, HttpStatus.OK);
  }

}
