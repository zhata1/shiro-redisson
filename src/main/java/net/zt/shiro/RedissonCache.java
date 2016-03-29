package net.zt.shiro;

import net.zt.redis.RedissonManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.redisson.core.RMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedissonCache<K, V> implements Cache<K, V> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RedissonManager redissonManager;
    private String shiroCacheKey = "shiro_cache";
    private long ttl = 20L;//time to tive(minutes)
    private long mit = 10L;//max idle time(minutes)

    public RedissonCache(RedissonManager redissonManager){
        if (redissonManager == null) {
            throw new IllegalArgumentException("redissonManager cannot be null.");
        }
        this.redissonManager = redissonManager;
    }

    public RedissonCache(RedissonManager redissonManager, String shiroCacheKey){
        this(redissonManager);
        this.shiroCacheKey = shiroCacheKey;
    }

    public V get(K key) throws CacheException {
        try {
            if (key == null) {
                return null;
            }else{
                RMapCache<K, V> mapCache = redissonManager.getMapCache(shiroCacheKey);
                return mapCache.get(key);
            }
        } catch (Exception e) {
            throw new CacheException(e);
        }

    }

    public V put(K key, V value) throws CacheException {
        try {
            RMapCache<K, V> mapCache = redissonManager.getMapCache(shiroCacheKey);
            mapCache.put(key, value, ttl, TimeUnit.MINUTES, mit, TimeUnit.MINUTES);
            return value;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public V remove(K key) throws CacheException {
        try {
            RMapCache<K, V> mapCache = redissonManager.getMapCache(shiroCacheKey);
            V previous =mapCache.remove(key);
            return previous;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public void clear() throws CacheException {
        try {
            RMapCache<K, V> mapCache = redissonManager.getMapCache(shiroCacheKey);
            mapCache.clear();
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public int size() {
        try {
            RMapCache<K, V> mapCache = redissonManager.getMapCache(shiroCacheKey);
            return mapCache.size();
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Set<K> keys() {
        try {
            Set<K> set = null;
            RMapCache<K, V> mapCache = redissonManager.getMapCache(shiroCacheKey);
            if(mapCache != null && mapCache.size()>0){
                set = new HashSet<K>(mapCache.size());
                Set<Map.Entry<K, V>> entrySet = mapCache.entrySet();
                for (Map.Entry<K, V> entry : entrySet) {
                    set.add(entry.getKey());
                }
            }
            return set;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Collection<V> values() {
        try {
            Set<V> set = null;
            RMapCache<K, V> mapCache = redissonManager.getMapCache(shiroCacheKey);
            if(mapCache != null && mapCache.size()>0){
                set = new HashSet<V>(mapCache.size());
                Set<Map.Entry<K, V>> entrySet = mapCache.entrySet();
                for (Map.Entry<K, V> entry : entrySet) {
                    set.add(entry.getValue());
                }
            }
            return set;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public String getShiroCacheKey() {
        return shiroCacheKey;
    }

    public void setShiroCacheKey(String shiroCacheKey) {
        this.shiroCacheKey = shiroCacheKey;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getMit() {
        return mit;
    }

    public void setMit(long mit) {
        this.mit = mit;
    }

    public RedissonManager getRedissonManager() {
        return redissonManager;
    }

    public void setRedissonManager(RedissonManager redissonManager) {
        this.redissonManager = redissonManager;
    }
}
