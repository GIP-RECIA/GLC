package fr.recia.glc.services.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidationService {

    private final CacheManager cacheManager;

    public CacheInvalidationService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictPersonneAndAssociatedStructures(Long personneId, Long structureId) {
        evict("personneFonctions", personneId);
        evict("personne", personneId);
        evict("personnesWithoutFunctions", structureId);
        evict("structureFonctions", structureId);
        evict("personnesByEtablissement", new SimpleKey(structureId, true));
        evict("personnesByEtablissement", new SimpleKey(structureId, false));
    }

    private void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }
}
