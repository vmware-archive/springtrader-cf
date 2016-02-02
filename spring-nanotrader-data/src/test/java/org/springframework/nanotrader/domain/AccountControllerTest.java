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

import static org.junit.Assert.assertNotNull;

@WebIntegrationTest(value = "server.port=9873")
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
public class AccountControllerTest {

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
        Long id = profile.getAccounts().get(0).getAccountid();
        assertNotNull(id);
        Account obj = accountService.findAccount(id);
        assertNotNull("Should find a result.", obj);

        assertNotNull(obj.getAccountid());
        assertNotNull(obj.getBalance());
        assertNotNull(obj.getCreationdate());
        assertNotNull(obj.getLogincount());
        assertNotNull(obj.getLogoutcount());
        assertNotNull(obj.getOpenbalance());
        assertNotNull(obj.getVersion());
    }

    @Test
    public void testFindByProfile() {
        Accountprofile ap = accountProfileService
                .findAccountProfile(profile.getProfileid());
        assertNotNull(ap);

        Account obj = accountService.findByProfile(ap);
        assertNotNull("Should find a result.", obj);
    }

    @Test
    public void testSaveAndDelete() {
        Accountprofile ap = accountProfileService
                .findAccountProfile(profile.getProfileid());
        assertNotNull(ap);

        Account a = new Account();
        a.setAccountprofile(ap);
        a = accountService.saveAccount(a);
        assertNotNull(a);
        Long id = a.getAccountid();
        assertNotNull(id);

        accountService.deleteAccount(a);
    }
}
