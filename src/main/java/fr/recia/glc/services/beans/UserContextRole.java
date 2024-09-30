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
package fr.recia.glc.services.beans;

import com.google.common.collect.Maps;
import fr.recia.glc.ldap.StructureKey;
import fr.recia.glc.ldap.enums.PermissionType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald
 * <p>
 * Should be used as session bean
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class UserContextRole {

  private final Map<StructureKey, PermissionType> contexts = Maps.newConcurrentMap();

  @Setter
  private Boolean superAdmin;

  private boolean userRolesLoaded;

  /**
   * Estimated duration before a reload can be done, here 10 seconds.
   * It's important for performance to avoid too much useless reload.
   * More several reload are made when the user connect to several async call
   * requesting user permissions and contexts
   */
  private final Duration duration = Duration.ofSeconds(10);
  @Setter
  private volatile Instant expiringInstant;
  private volatile boolean loadingInProgress = false;

  public UserContextRole() {
    super();
  }

  /**
   * To construct the Roles, use this add on all context.
   *
   * @param ctx      the context to add discovered.
   * @param permType The Role that the user has in the context.
   */
  public synchronized void addCtx(@NotNull final StructureKey ctx, final PermissionType permType) {
    if (!isCtxLoaded(ctx)) contexts.put(ctx, permType);
  }

  /**
   * Return the Role for a context. All structure must be loaded.
   *
   * @param ctx
   * @return
   */
  public PermissionType getRoleFromContext(@NotNull final StructureKey ctx) {
    if (userRolesLoaded) {
      if (this.superAdmin) return PermissionType.ADMIN;
      if (contexts.containsKey(ctx)) return contexts.get(ctx);
      log.warn("Call getRoleFromContext - StructureKey {} not found in User Roles !", ctx);
    } else log.warn("Call getRoleFromContext - User Roles are not loaded !");

    return null;
  }

  public boolean hasContextRoles() {
    return !this.contexts.isEmpty();
  }

  public boolean isCtxLoaded(final StructureKey ctx) {
    return contexts.containsKey(ctx) && contexts.get(ctx) != null;
  }

  public boolean areRolesLoaded() {
    return superAdmin != null && userRolesLoaded;
  }

  public boolean areRolesLoadInProgress() {
    return loadingInProgress;
  }

  public boolean loadingCanBeDone() {
    return (this.expiringInstant == null || this.expiringInstant.isBefore(Instant.now())) && !this.loadingInProgress;
  }

  public void processingLoading() {
    loadingInProgress = true;
    contexts.clear();
    superAdmin = null;
    userRolesLoaded = false;
    log.debug("============ >>>>>>> processingLoading");
  }

  public void notifyEndLoading() {
    userRolesLoaded = true;
    // we set this expiration time to avoid to make several reload at the same time
    expiringInstant = Instant.now().plus(duration);
    loadingInProgress = false;
    log.debug("============ >>>>>>> notifyEndLoading");
  }

  public Set<StructureKey> allowedStructures() {
    return contexts.keySet();
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("UserContextRoles {" +
      "superAdmin=" + superAdmin +
      ", userRolesLoaded=" + userRolesLoaded +
      ", contexts= [");

    for (Map.Entry<StructureKey, PermissionType> entry : contexts.entrySet()) {
      str
        .append("\n{")
        .append(entry.getKey())
        .append("=")
        .append(entry.getValue())
        .append("}");
    }
    if (!contexts.entrySet().isEmpty()) str.append("\n");
    str.append("]}");

    return str.toString();
  }

}
