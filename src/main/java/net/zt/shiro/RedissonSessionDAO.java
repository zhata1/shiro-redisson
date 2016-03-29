package net.zt.shiro;

import net.zt.redis.RedissonManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.redisson.core.RMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zt on 2016/3/29.
 */
public class RedissonSessionDAO extends AbstractSessionDAO {

    private static Logger logger = LoggerFactory.getLogger(RedissonSessionDAO.class);

    private RedissonManager redissonManager;
    private String shiroSessionKey = "shiro_session";
    private long ttl = 30L;//time to tive(minutes)
    private long mit = 30L;//max idle time(minutes)

    public void update(Session session) throws UnknownSessionException {
        this.saveSession(session);
    }

    private void saveSession(Session session) throws UnknownSessionException{
        if(session == null || session.getId() == null){
            logger.error("session or session id is null");
            return;
        }
        RMapCache<Serializable, Session> mapCache =  redissonManager.getMapCache(this.shiroSessionKey);
        mapCache.put(session.getId(), session, ttl, TimeUnit.MINUTES, mit, TimeUnit.MINUTES);
    }

    public void delete(Session session) {
        if(session == null || session.getId() == null){
            logger.error("session or session id is null");
            return;
        }
        RMapCache<Serializable, Session> mapCache =  redissonManager.getMapCache(this.shiroSessionKey);
        mapCache.remove(session.getId());

    }

    public Collection<Session> getActiveSessions() {
        Set<Session> sessions = null;
        RMapCache<Serializable, Session> mapCache = redissonManager.getMapCache(this.shiroSessionKey);
        if(mapCache != null && mapCache.size()>0){
            sessions = new HashSet<Session>(mapCache.size());
            Set<Map.Entry<Serializable, Session>> entrySet = mapCache.entrySet();
            for (Map.Entry<Serializable, Session> entry : entrySet) {
                sessions.add(entry.getValue());
            }
        }
        return sessions;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if(sessionId == null){
            logger.error("session id is null");
            return null;
        }
        RMapCache<Serializable, Session> mapCache = redissonManager.getMapCache(this.shiroSessionKey);
        return mapCache.get(sessionId);
    }

    public RedissonManager getRedissonManager() {
        return redissonManager;
    }

    public void setRedissonManager(RedissonManager redissonManager) {
        this.redissonManager = redissonManager;
        //init redissonManager
        this.redissonManager.init();
    }

    public String getShiroSessionKey() {
        return shiroSessionKey;
    }

    public void setShiroSessionKey(String shiroSessionKey) {
        this.shiroSessionKey = shiroSessionKey;
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
}