package net.zt.redis;

import org.redisson.*;
import org.redisson.core.RMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Redis 管理类
 * Created by zt on 2016/3/28.
 */
public class RedissonManager {

    private static final Logger logger = LoggerFactory.getLogger(RedissonManager.class);

    private static RedissonClient client;
    private List<String> clusterIps;
    private String masterIp;
    private List<String> slaveIps;

    public void init() {
        if (client == null) {
            Config config = new Config();
            if (masterIp != null && !masterIp.isEmpty()) {
                if (slaveIps != null && !slaveIps.isEmpty()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Redis use MasterSlaveServersConfig:{}", list2Str(slaveIps));
                    }
                    MasterSlaveServersConfig cfg = config.useMasterSlaveServers();
                    cfg.setRetryAttempts(Integer.MAX_VALUE);
                    for (String ip : slaveIps) {
                        cfg.addSlaveAddress(ip);
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Redis use SingleServersConfig:{}", masterIp);
                    }
                    config.useSingleServer().setAddress(masterIp);
                    config.useSingleServer().setRetryAttempts(Integer.MAX_VALUE);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Redis use ClusterServersConfig:{}", list2Str(clusterIps));
                }
                ClusterServersConfig cfg = config.useClusterServers();
                cfg.setRetryAttempts(Integer.MAX_VALUE);
                for (String ip : clusterIps) {
                    cfg.addNodeAddress(ip);
                }
            }
            client = Redisson.create(config);
        }
    }

    public void destory() {
        client.shutdown();
    }

    synchronized public RedissonClient getClient() {
        return client;
    }

    public void setClusterIps(List<String> ips) {
        this.clusterIps = ips;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }

    public void setSlaveIps(List<String> slaveIps) {
        this.slaveIps = slaveIps;
    }

    public <K, V> RMapCache<K, V> getMapCache(String name) {
        return client.getMapCache(name);
    }
//    public <K, V> RMap<K, V> getMap(String name) {
//        return client.getMap(name);
//    }
//
//    public <V> RBucket<V> getBucket(String name) {
//        return client.getBucket(name);
//    }
//
//    public <V> RSet<V> getSet(String name) {
//        return client.getSet(name);
//    }

    private String list2Str(List<String> list){
        StringBuffer sb = new StringBuffer(list.size());
        for (String str:list) {
            sb.append(str).append("|");
        }
        return sb != null && sb.length() > 0 ? sb.substring(sb.length()) : "NULL";
    }
}
