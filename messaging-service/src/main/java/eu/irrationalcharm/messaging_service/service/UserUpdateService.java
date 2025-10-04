package eu.irrationalcharm.messaging_service.service;


import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.springframework.stereotype.Service;



/**
 * Used to evict cache
 */
@Service
public class UserUpdateService {

    private final Cache userGraphCache;

    public UserUpdateService(CacheManager cacheManager) {
        this.userGraphCache = cacheManager.getCache("user-graph");
    }


    public void evictUserGraph(String username, String providerId) {

        if (username != null) {
            userGraphCache.evictIfPresent(username);
        }


        if(providerId != null)
            userGraphCache.evictIfPresent(providerId);
    }
}
