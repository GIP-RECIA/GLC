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

import com.google.common.collect.Sets;
import fr.recia.glc.ldap.IStructure;
import fr.recia.glc.ldap.repository.IExternalGroupDao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@NoArgsConstructor
@Slf4j
public class StructureLoaderImpl implements IStructureLoader, InitializingBean {

  Set<IStructure> loadedStructures = new HashSet<>();

  Map<String, Set<IStructure>> loadedStructuresByBranch = new HashMap<>();

  @Setter
  private IExternalGroupDao groupDao;

  public StructureLoaderImpl(IExternalGroupDao groupDao) {
    this.groupDao = groupDao;
  }

  @PostConstruct
  private void loadingStructures() {
    init();
  }

  private void init() {
    loadedStructures = groupDao.getStructuresFromGroups();
    loadedStructures.forEach(structure -> {
      if (loadedStructuresByBranch.containsKey(structure.getGroupBranch()))
        loadedStructuresByBranch.get(structure.getGroupBranch()).add(structure);
      else loadedStructuresByBranch.put(structure.getGroupBranch(), Sets.newHashSet(structure));
    });
    log.debug(
      "Loaded Structure: {}",
      loadedStructuresByBranch.keySet().stream()
        .map(key -> {
          List<String> values = loadedStructuresByBranch.get(key).stream()
            .map(structure -> "\n\t\t\t{" +
              "\n\t\t\t\t\"keyId\": \"" + structure.getStructureKey().getKeyId() + "\"" +
              ",\n\t\t\t\t\"keyType\": \"" + structure.getStructureKey().getKeyType() + "\"" +
              ",\n\t\t\t\t\"UAI\": \"" + structure.getUAI() + "\"" +
              ",\n\t\t\t\t\"displayName\": \"" + structure.getDisplayName() + "\"" +
              ",\n\t\t\t\t\"groupBranch\": \"" + structure.getGroupBranch() + "\"" +
              ",\n\t\t\t\t\"groupNameEtab\": \"" + structure.getGroupNameEtab() + "\"" +
              "\n\t\t\t}"
            )
            .collect(Collectors.toList());

          return "\t{" +
            "\n\t\t\"" + key + "\": " + values +
            "\n\t}";
        })
        .collect(Collectors.joining(",\n", "[\n", "\n]"))
    );
    log.debug(
      "Loaded Structure recap: {}",
      loadedStructuresByBranch.keySet().stream()
        .map(key -> "\t{ \"" + key + "\": " + loadedStructuresByBranch.get(key).size() + " }")
        .collect(Collectors.joining(",\n", "[\n", "\n]"))
    );
  }

  @Override
  public Set<IStructure> getStructuresOfBranch(String branchGroup) {
    return loadedStructuresByBranch.getOrDefault(branchGroup, Collections.emptySet());
  }

  @Override
  public Set<IStructure> getAllStructures() {
    return loadedStructures;
  }

  /* managing structure refresh like a cache but on managed way like on esco-ChangeEtablissement */

  /**
   * Cache Read / Write lock.
   */
  private final ReentrantReadWriteLock cacheRwl = new ReentrantReadWriteLock();
  private final Lock cacheRl = cacheRwl.readLock();
  private final Lock cacheWl = cacheRwl.writeLock();

  /**
   * Instant when the cache will be expiring.
   */
  @Getter
  @Setter
  protected volatile Instant expiringInstant;

  @Getter
  @Setter
  private Cache c1;

  @Getter
  @Setter
  private Cache c2;

  /**
   * Configured caching duration (default 1 hour).
   */
  private Duration cachingDuration = Duration.standardHours(1L);

  /**
   * Configured caching duration (default 3 second).
   */
  private Duration refreshExpiredDuration = Duration.standardSeconds(3);

  /**
   * True if cache is loading.
   */
  private volatile boolean loadingInProgress = false;

//  protected synchronized void reload() {
//    // Test if another concurrent thread just didn't already load the cache
//    if (this.cacheLoadingNeeded()) {
//      this.loadingInProgress = true;
//      log.debug("Loading structure cache...");
//
//      init();
//    }
//  }

  /**
   * Test if a cache loading is needed.
   * Cache loading is needed if Cache is not initialized or is expired and no loading is already in progress.
   *
   * @return true if cache loading is needed.
   */
  protected boolean cacheLoadingNeeded() {
    return (this.expiringInstant == null || this.expiringInstant.isBeforeNow()) && !this.loadingInProgress;
  }

  /**
   * Getter of cachingDuration.
   *
   * @return the cachingDuration
   */
  public long getCachingDuration() {
    return this.cachingDuration.getMillis();
  }

  /**
   * Setter of cachingDuration (in ms).
   *
   * @param cachingDuration the cachingDuration to set
   */
  public void setCachingDuration(final long cachingDuration) {
    this.cachingDuration = Duration.millis(cachingDuration);
  }

  /**
   * Getter of refreshExpiredDuration
   *
   * @return the refreshExpiredDuration
   */
  public long getRefreshExpiredDuration() {
    return refreshExpiredDuration.getMillis();
  }

  /**
   * Setter of refreshExpiredDuration
   *
   * @param refreshExpiredDuration the refreshExpiredDuration to set
   */
  public void setRefreshExpiredDuration(final long refreshExpiredDuration) {
    this.refreshExpiredDuration = Duration.millis(refreshExpiredDuration);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
//    Assert.notNull(this.groupDao, "No IExternalGroupDao configured !");
//    Assert.notNull(this.c1, "No c1 cache configured !");
//    Assert.notNull(this.c2, "No c2 cache configured !");
  }

}
