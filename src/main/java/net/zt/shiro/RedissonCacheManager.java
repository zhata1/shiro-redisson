package net.zt.shiro;//package net.zt.shiro;

import net.zt.redis.RedissonManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MapCache;
import org.redisson.core.RMap;
import org.redisson.core.RMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Created by zt on 2016/3/28.
 */
public class RedissonCacheManager implements CacheManager {
    private static final Logger logger = LoggerFactory .getLogger(RedissonCacheManager.class);
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();

    private RedissonManager redissonManager;
    private String shiroCacheManagerKey = "shiro_cache";

    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        Cache cache = caches.get(name);
        if (cache == null) {
            redissonManager.init();
            cache = new RedissonCache<K, V>(redissonManager,shiroCacheManagerKey);
            caches.put(name,cache);
        }
        return cache;
    }

    public String getShiroCacheManagerKey() {
        return shiroCacheManagerKey;
    }

    public void setShiroCacheManagerKey(String shiroCacheManagerKey) {
        this.shiroCacheManagerKey = shiroCacheManagerKey;
    }

    public RedissonManager getRedissonManager() {
        return redissonManager;
    }

    public void setRedissonManager(RedissonManager redissonManager) {
        this.redissonManager = redissonManager;
    }
}
