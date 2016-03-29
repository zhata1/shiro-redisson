package net.zt.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisCacheManagerTest{

    final static Logger log = LoggerFactory .getLogger(RedisCacheManagerTest.class);

    @Before
    public void setUp() throws Exception {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }

    @Test
    public void testUserLogin() throws Exception {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(
                "guest", "guest");
        token.setRememberMe(true);
        subject.login(token);

        log.info("User successfuly logged in");
    }

    @After
    public void tearDown() throws Exception {
        // logout the subject
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
    }
}