package org.springframework.nanotrader.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.domain.test.OrderDataOnDemand;
import org.springframework.nanotrader.data.service.OrderService;
import org.springframework.nanotrader.service.Config;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class OrderIntegrationTest {

	
    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private OrderDataOnDemand dod;

	@Autowired
    OrderService orderService;

	@Test
    public void testCountAllOrders() {
        Assert.assertNotNull("Data on demand for 'Order' failed to initialize correctly", dod.getRandomOrder());
        long count = orderService.countAllOrders();
        Assert.assertTrue("Counter for 'Order' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindOrder() {
        Order obj = dod.getRandomOrder();
        Assert.assertNotNull("Data on demand for 'Order' failed to initialize correctly", obj);
        Long id = obj.getOrderid();
        Assert.assertNotNull("Data on demand for 'Order' failed to provide an identifier", id);
        obj = orderService.find(id);
        Assert.assertNotNull("Find method for 'Order' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Order' returned the incorrect identifier", id, obj.getOrderid());
    }

	@Test
    public void testFindAllOrders() {
        Assert.assertNotNull("Data on demand for 'Order' failed to initialize correctly", dod.getRandomOrder());
        long count = orderService.countAllOrders();
        Assert.assertTrue("Too expensive to perform a find all test for 'Order', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Order> result = orderService.findAllOrders();
        Assert.assertNotNull("Find all method for 'Order' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Order' failed to return any data", result.size() > 0);
    }

	@Test
    public void testSaveOrder() {
        Assert.assertNotNull("Data on demand for 'Order' failed to initialize correctly", dod.getRandomOrder());
        Order obj = dod.getNewTransientOrder(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Order' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Order' identifier to be null", obj.getOrderid());
        orderService.saveOrder(obj);
        Assert.assertNotNull("Expected 'Order' identifier to no longer be null", obj.getOrderid());
    }
}
