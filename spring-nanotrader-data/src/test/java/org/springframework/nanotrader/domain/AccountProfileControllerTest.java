package org.springframework.nanotrader.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.nanotrader.data.service.AccountService;
import org.springframework.nanotrader.data.service.FallBackAccountProfileService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;

@WebIntegrationTest(value = "server.port=9873")
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
public class AccountProfileControllerTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountProfileService accountProfileService;

    private Accountprofile profile;

    @Before
    public void setUp() {
        Accountprofile ap = accountProfileService.saveAccountProfile(FallBackAccountProfileService.fakeAccountProfile(true));
        assertNotNull(ap);
        profile = ap;
    }

    @Test
    public void testFind() {
        Long id = profile.getProfileid();
        assertNotNull(id);

        Accountprofile obj = accountProfileService
                .findAccountProfile(id);
        assertNotNull(obj);

        assertNotNull(obj.getAccounts());
        assertTrue(obj.getAccounts().size() > 0);
        assertNotNull(obj.getAddress());
        assertNotNull(obj.getAuthtoken());
        assertNotNull(obj.getCreditcard());
        assertNotNull(obj.getEmail());
        assertNotNull(obj.getFullname());
        assertNotNull(obj.getPasswd());
        assertEquals(id, obj.getProfileid());
        assertNotNull(obj.getUserid());

        obj = accountProfileService.findAccountProfile(12345L);
        assertNull(obj);
    }

    @Test
    public void testFindByAuth() {
        Accountprofile obj = accountProfileService.findByAuthtoken(profile.getAuthtoken());
        assertNotNull(obj);
        assertEquals("invalid authtoken", obj.getAuthtoken());
        assertNotNull(obj.getAccounts());
        assertTrue(obj.getAccounts().size() > 0);
        obj = accountProfileService.findByAuthtoken("foo");
        assertNull(obj);
    }

    @Test
    public void testFindByUserId() {
        Accountprofile obj = accountProfileService.findByUserid(profile.getUserid());
        assertNotNull(obj);
        assertEquals("fake", obj.getUserid());
        obj = accountProfileService.findByUserid("foo");
        assertNull(obj);
    }

    @Test
    public void testFindByUserIdAndPassword() {
        Accountprofile obj = accountProfileService.findByUseridAndPasswd(profile.getUserid(), profile.getPasswd());
        assertNotNull(obj);
        assertEquals("fake", obj.getUserid());
        assertEquals("fake", obj.getPasswd());
        obj = accountProfileService.findByUseridAndPasswd("foo", "test");
        assertNull(obj);
        obj = accountProfileService.findByUseridAndPasswd("test", "foo");
        assertNull(obj);
    }

    @Test
    public void testSaveAccountAndDelete() {
        Accountprofile ap = new Accountprofile();
        ap.setUserid("deleteMe2:" + System.currentTimeMillis());
        ap.setAddress("address");
        ap.setAuthtoken("authtoken");
        ap.setCreditcard("creditcard");
        ap.setEmail("email");
        ap.setFullname("fullname");
        ap.setPasswd("passwd");

        ap = accountProfileService.saveAccountProfile(ap);
        assertNotNull(ap);

        Long id = ap.getProfileid();
        ap = accountProfileService.findAccountProfile(id);
        assertNotNull(ap);

        Account a = new Account();
        a.setAccountprofile(ap);
        ap.setAccounts(new ArrayList<Account>());
        ap.getAccounts().add(a);
        a.setBalance(new BigDecimal(123));
        accountService.saveAccount(a);

        accountProfileService.deletelAccountProfile(ap);

        ap = accountProfileService.findAccountProfile(id);
        assertNull(ap);
    }

}
