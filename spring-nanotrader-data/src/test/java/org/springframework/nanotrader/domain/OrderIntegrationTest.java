package org.springframework.nanotrader.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.Config;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.service.OrderService;
import org.springframework.nanotrader.service.FallBackHoldingService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class OrderIntegrationTest {

    @Autowired
    OrderService orderService;

    @Autowired
    FallBackHoldingService holdingService;

    @Before
    public void setUp() {
        orderService.saveOrder(holdingService.fakeHolding(true).getOrders().get(0));
    }

    @Test
    public void testCountAllOrders() {
        long count = orderService.countAllOrders();
        Assert.assertTrue("Counter for 'Order' incorrectly reported there were no entries", count > 0);
    }

    @Test
    public void testFindOrders() {
        List<Order> result = orderService.findAllOrders();
        Assert.assertNotNull("Find all method for 'Order' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Order' failed to return any data", result.size() > 0);

        Long id = result.get(0).getOrderid();
        Order obj = orderService.find(id);
        Assert.assertNotNull(obj);
        Assert.assertEquals(id, obj.getOrderid());
    }

    @Test
    public void testSaveOrder() {
        Order obj = orderService.saveOrder(new Order());
        Assert.assertNotNull("Expected 'Order' identifier to no longer be null", obj.getOrderid());
    }
}
