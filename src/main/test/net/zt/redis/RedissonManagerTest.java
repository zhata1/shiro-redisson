package net.zt.redis;

import net.zt.shiro.UserMock;
import org.junit.Test;
import org.redisson.core.RMapCache;
/**
 * Created by simpletour on 2016/3/29.
 */
public class RedissonManagerTest {
    @Test
    public void testSet(){

        RedissonManager redisManager  = new RedissonManager();
        redisManager.setMasterIp("127.0.0.1:6379");
        redisManager.init();

        String key = "abc";
        UserMock u = new UserMock();
        u.setId("123");
        u.setLocked(true);
        u.setPassword("111");
        u.setSalt("222");
        u.setUsername("jack");

        RMapCache<String,UserMock> mc = redisManager.getMapCache("test");
        mc.put(key,u);
    }
}
