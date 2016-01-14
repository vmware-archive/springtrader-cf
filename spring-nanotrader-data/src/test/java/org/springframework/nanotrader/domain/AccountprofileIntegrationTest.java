package org.springframework.nanotrader.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.domain.test.AccountprofileDataOnDemand;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
 
@Transactional
public class AccountprofileIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private AccountprofileDataOnDemand dod;

	@Autowired
    AccountProfileService accountProfileService;

	@PersistenceContext
	EntityManager entityManager;

	@Test
    public void testCount() {
        Assert.assertNotNull("Data on demand for 'Accountprofile' failed to initialize correctly", dod.getRandomAccountprofile());
        long count = accountProfileService.countAllAccountProfiles();
        Assert.assertTrue("Counter for 'Accountprofile' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFind() {
        Accountprofile obj = dod.getRandomAccountprofile();
        Assert.assertNotNull("Data on demand for 'Accountprofile' failed to initialize correctly", obj);
        Long id = obj.getProfileid();
        Assert.assertNotNull("Data on demand for 'Accountprofile' failed to provide an identifier", id);
        obj = accountProfileService.findAccountProfile(id);
        Assert.assertNotNull("Find method for 'Accountprofile' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Accountprofile' returned the incorrect identifier", id, obj.getProfileid());
    }

	@Test
    public void testFindAll() {
        Assert.assertNotNull("Data on demand for 'Accountprofile' failed to initialize correctly", dod.getRandomAccountprofile());
        long count = accountProfileService.countAllAccountProfiles();
        Assert.assertTrue("Too expensive to perform a find all test for 'Accountprofile', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Accountprofile> result = accountProfileService.findAllAccountProfiles();
        Assert.assertNotNull("Find all method for 'Accountprofile' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Accountprofile' failed to return any data", result.size() > 0);
    }

	@Test
    public void testSave() {
        Assert.assertNotNull("Data on demand for 'Accountprofile' failed to initialize correctly", dod.getRandomAccountprofile());
        Accountprofile obj = dod.getNewTransientAccountprofile(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Accountprofile' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Accountprofile' identifier to be null", obj.getProfileid());
        accountProfileService.saveAccountProfile(obj);
        Assert.assertNotNull("Expected 'Accountprofile' identifier to no longer be null", obj.getProfileid());
    }

	@Test
    public void testDelete() {
        Accountprofile obj = dod.getNewTransientAccountprofile(100);
        accountProfileService.saveAccountProfile(obj);
        entityManager.flush();
        entityManager.clear();

        Assert.assertNotNull("Data on demand for 'Accountprofile' failed to initialize correctly", obj);
        Long id = obj.getProfileid();
        Assert.assertNotNull("Data on demand for 'Accountprofile' failed to provide an identifier", id);
        obj = accountProfileService.findAccountProfile(id);
        accountProfileService.deletelAccountProfile(obj);
        Assert.assertNull("Failed to remove 'Accountprofile' with identifier '" + id + "'", accountProfileService.findAccountProfile(id));
    }
}
