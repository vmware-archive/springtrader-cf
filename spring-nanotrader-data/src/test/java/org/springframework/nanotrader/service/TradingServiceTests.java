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
package org.springframework.nanotrader.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.nanotrader.data.domain.*;
import org.springframework.nanotrader.data.domain.test.HoldingDataOnDemand;
import org.springframework.nanotrader.data.domain.test.OrderDataOnDemand;
import org.springframework.nanotrader.data.service.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Gary Russell
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
public class TradingServiceTests {

    @Autowired
    private HoldingDataOnDemand holdingDataOnDemand;

    @Autowired
    private OrderDataOnDemand orderDataOnDemand;

    @Autowired
    private TradingService tradingService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountProfileService accountProfileService;

    @Autowired
    OrderService orderService;

    @Autowired
    HoldingService holdingService;

    @Autowired
    @Qualifier("rtQuoteService")
    QuoteService quoteService;

    Accountprofile profile;

    @Before
    public void setUp() {
        Accountprofile ap = FallBackAccountProfileService.fakeAccountProfile(true);
        profile = accountProfileService.saveAccountProfile(ap);
    }

    @Test
    public void testFindHoldingsByAccount() {
        Integer page = 0;
        Integer pageSize = 10;
        Holding holding100 = holdingDataOnDemand.getNewTransientHolding(100);
        Holding holding101 = holdingDataOnDemand.getNewTransientHolding(101);
        holding101.setAccountAccountid(holding100.getAccountAccountid());
        holdingService.save(holding100);
        holdingService.save(holding101);

        List<Holding> holdings = holdingService.findByAccountid(holding100.getAccountAccountid());
        assertEquals(2, holdings.size());
        Map<Long, Holding> map = new HashMap<Long, Holding>();
        map.put(holdings.get(0).getHoldingid(), holdings.get(0));
        map.put(holdings.get(1).getHoldingid(), holdings.get(1));
        assertNotNull(map.remove(holding100.getHoldingid()));
        assertNotNull(map.remove(holding101.getHoldingid()));
    }

    @Test
    public void testSaveAndFindAndUpdateHolding() {
        Holding holding = holdingDataOnDemand.getNewTransientHolding(100);
        holding.setPurchasedate(new java.sql.Date(System.currentTimeMillis()));
        holdingService.save(holding);

        Holding newHolding = holdingService.find(holding.getHoldingid());
        assertEquals(holding.toString(), newHolding.toString());

        newHolding.setPurchaseprice(BigDecimal.valueOf(1234.56));
        holdingService.save(newHolding);

        Holding updatedHolding = holdingService.find(holding.getHoldingid());

        assertEquals(newHolding.toString(), updatedHolding.toString());

    }

    @Test
    public void testFindAccountSummary() {
        Holding holding = holdingDataOnDemand.getNewTransientHolding(100);
        holding.setPurchasedate(new java.sql.Date(System.currentTimeMillis()));
        holding.setQuoteSymbol("GOOG");
        holdingService.save(holding);
        PortfolioSummary portfolioSummary = holdingService.findPortfolioSummary(100L);
        Assert.assertTrue("Expected 'PortfolioSummary' holding count to be equal to 1", portfolioSummary.getNumberOfHoldings() == 1);
    }

    @Test
    public void testHoldingAggregateSummary() {
        Holding holding = holdingDataOnDemand.getNewTransientHolding(102);
        holding.setPurchasedate(new java.sql.Date(System.currentTimeMillis()));
        holding.setQuoteSymbol("GOOG");
        holdingService.save(holding);
        HoldingSummary holdingSummary = holdingService.findHoldingSummary(new Long(102));
        Assert.assertNotNull(holdingSummary);
        Assert.assertTrue(holdingSummary.getHoldingsTotalGains().floatValue() != 0.0f);
    }


    @Test
    public void testSaveAndFindOrder() {
        Order order = orderDataOnDemand.getNewTransientOrder(100);
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

    @Test

    public void testFindMarketSummary() {

        Quote quote = new Quote();
        quote.setSymbol("symbol1");
        quote.setPrice(BigDecimal.valueOf(50.01));
        quote.setChange1(BigDecimal.valueOf(5.00));
        quote.setVolume(BigDecimal.valueOf(50000));
        quote.setChange1(BigDecimal.valueOf(4.00));
        quote.setOpen1(BigDecimal.valueOf(49.00));
        quoteService.saveQuote(quote);
        Quote quote2 = new Quote();
        quote2.setSymbol("symbol2");
        quote2.setPrice(BigDecimal.valueOf(150.00));
        quote2.setChange1(BigDecimal.valueOf(15.00));
        quote2.setVolume(BigDecimal.valueOf(150000));
        quote2.setChange1(BigDecimal.valueOf(4.00));
        quote2.setOpen1(BigDecimal.valueOf(120.00));
        quoteService.saveQuote(quote2);
        MarketSummary marketSummary = quoteService.marketSummary();
        // need to harden this test!!
        Assert.assertNotNull("Expected 'MarketSummary' Market Volume should not be null", marketSummary.getTradeStockIndexVolume());


    }


}
