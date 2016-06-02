/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.nanotrader.service.support;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.nanotrader.data.service.AccountService;
import org.springframework.nanotrader.data.service.HoldingService;
import org.springframework.nanotrader.data.service.OrderService;
import org.springframework.nanotrader.service.FallBackAccountProfileService;
import org.springframework.nanotrader.service.FallBackHoldingService;
import org.springframework.nanotrader.service.support.config.IntegrationTestConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Gary Russell
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestConfig.class)
public class TradingServiceTests {

    @Autowired
    private TradingService tradingService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private HoldingService holdingService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountProfileService accountprofileService;

    private Order order;

    private Account account;

    @Before
    public void setUp() {
        Holding h = ((FallBackHoldingService) holdingService).fakeHolding(true);
        Holding holding = holdingService.save(h);
        order = orderService.saveOrder(holding.getOrders().get(0));

        Accountprofile ap = FallBackAccountProfileService.fakeAccountProfile(true);
        ap = accountprofileService.saveAccountProfile(ap);
        account = accountService.saveAccount(ap.getAccounts().get(0));
    }


    @Test
    public void testSaveAndFindOrder() {
        order.setQuoteid("GOOG");
        order.setAccountid(account.getAccountid());
        order.setOrdertype(TradingService.ORDER_TYPE_BUY);
        order.setOpendate(new java.sql.Date(System.currentTimeMillis()));
        order.setCompletiondate(new java.sql.Date(System.currentTimeMillis()));
        tradingService.saveOrder(order);

        Order foundOrder = orderService.find(order.getOrderid());
        assertNotNull(foundOrder);

        BigDecimal oldPrice = foundOrder.getPrice();
        foundOrder.setPrice(BigDecimal.valueOf(123.45));
        tradingService.saveOrder(foundOrder);

        Order updatedOrder = orderService.find(order.getOrderid());
        assertNotNull(updatedOrder);

        order.setPrice(oldPrice);
        tradingService.saveOrder(foundOrder);

        updatedOrder = orderService.find(order.getOrderid());
        assertEquals(foundOrder.toString(), updatedOrder.toString());
    }
}
