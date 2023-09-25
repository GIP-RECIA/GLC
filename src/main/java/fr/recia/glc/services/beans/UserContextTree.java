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
/**
 *
 */
package fr.recia.glc.services.beans;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import fr.recia.glc.ldap.StructureKey;
import fr.recia.glc.ldap.enums.PermissionType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald
 *
 *         Should be used as session bean
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class UserContextTree {

    private Map<StructureKey, PermissionType> contexts = Maps.newConcurrentMap();

    @Setter
    private Boolean superAdmin;

    private boolean userTreeLoaded;

    /**
     * Estimated duration before a reload can be done, here 10 seconds.
     * It's important for performance to avoid too much useless reload.
     * More several reload are made when the user connect to several async call
     * requesting user permissions and contexts
     * */
    private Duration duration = Duration.ofSeconds(10);
    @Setter
    private volatile Instant expiringInstant;
    private volatile boolean loadingInProgress = false;

    public UserContextTree() {
        super();
    }

    /**
     * To construct the tree use this add on all context.
     *
     * @param ctx
     *            the context to add discovered.
     * @param permType
     *            The Role that the user has in the context.
     */
    public synchronized void addCtx(@NotNull final StructureKey ctx, final PermissionType permType) {
        if (!isCtxLoaded(ctx)) {

            current.setLastLeaf(isLastNode);
            if (!ctx.getKeyType().equals(ContextType.ORGANIZATION)) {
                Assert.isTrue(parent != null && isCtxLoaded(parent),
                    "Context " + ctx.toString() + " doesn't have a parent = '" + parent.toString() + "' or parent isn't loaded !");
                contexts.put(ctx, current);
                this.linkToParent(ctx, parent);
            } else {
                contexts.put(ctx, current);
            }
        } else if (!ctx.getKeyType().equals(ContextType.ORGANIZATION)) {
            Assert.isTrue(parent != null && isCtxLoaded(parent),
                "Context " + ctx.toString() + " doesn't have a parent = '" + parent.toString() + "' or parent isn't loaded !");
            this.linkToParent(ctx, parent);
        }
    }

    /**
     * Return the Role for a context. All structure must be loaded.
     *
     * @param ctx
     * @return
     */
    public PermissionType getRoleFromContext(@NotNull final StructureKey ctx) {
        if (userTreeLoaded) {
            if (this.superAdmin) return PermissionType.ADMIN;
            if (contexts.containsKey(ctx)) return contexts.get(ctx);
            log.warn("Call getRoleFromContextTree - StructureKey {} not found in User Tree !", ctx);
        } else {
            log.warn("Call getRoleFromContextTree - User Tree is not loaded !");
        }
        return null;
    }

    public boolean isCtxLoaded(final StructureKey ctx) {
        return contexts.containsKey(ctx) && contexts.get(ctx) != null;
    }

    public boolean isTreeLoaded() {
        return superAdmin != null && userTreeLoaded;
    }

    public boolean isTreeLoadInProgress() {
        return loadingInProgress;
    }

    public boolean loadingCanBeDone() {
        return (this.expiringInstant == null || this.expiringInstant.isBefore(Instant.now())) && !this.loadingInProgress;
    }

    public void processingLoading() {
        loadingInProgress = true;
        contexts.clear();
        superAdmin = null;
        userTreeLoaded = false;
        log.debug("============ >>>>>>> processingLoading");
    }

    public void notifyEndLoading() {
        userTreeLoaded = true;
        // we set this expiration time to avoid to make several reload at the same time
        expiringInstant = Instant.now().plus(duration);
        loadingInProgress = false;
        log.debug("============ >>>>>>> notifyEndLoading");
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("UserContextTree{" +
            "superAdmin=" + superAdmin +
            ", userTreeLoaded=" + userTreeLoaded +
            ", contexts= [");

        for (Map.Entry<StructureKey, PermissionType> entry : contexts.entrySet()) {
            str.append("\n{").append(entry.getKey()).append("=").append(entry.getValue()).append("}");
        }
        if (!contexts.entrySet().isEmpty()) str.append("\n");
        str.append("]}");

        return str.toString();
    }
}
