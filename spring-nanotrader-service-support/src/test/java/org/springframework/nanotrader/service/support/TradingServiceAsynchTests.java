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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.nanotrader.data.service.OrderService;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.service.FallBackAccountProfileService;
import org.springframework.nanotrader.service.support.TradingServiceFacade;
import org.springframework.nanotrader.service.support.config.IntegrationTestConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * @author Gary Russell
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestConfig.class)
public class TradingServiceAsynchTests {

	@Autowired
	private TradingServiceFacade tradingServiceFacade;

	@Autowired
	private AccountProfileService accountProfileService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private QuoteService realTimeQuoteService;

	@Test
	public void testService() {
		Accountprofile ap = FallBackAccountProfileService.fakeAccountProfile(true);
		ap = accountProfileService.saveAccountProfile(ap);
		assertNotNull(ap);
		Long accountId = ap.getAccounts().get(0).getAccountid();
		assertNotNull(accountId);

		Quote quote = realTimeQuoteService.findBySymbol("GOOG");

		Order order = new Order();
		order.setOrdertype("buy");
		order.setAccountid(accountId);
		order.setQuote(quote);
		assertNotNull(order.getAccountid());
		Long i = tradingServiceFacade.saveOrder(order, true);
		assertNotNull(i);
	}
}
