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
package org.springframework.nanotrader.asynch.service;

import org.dozer.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.domain.test.OrderDataOnDemand;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.nanotrader.data.service.AccountService;
import org.springframework.nanotrader.service.FallBackAccountProfileService;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.service.domain.Order;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.service.support.TradingServiceFacade;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * @author Gary Russell
 *
 */
@Profile("test")
@ContextConfiguration(locations={
		"classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/spring-nanotrader-service-support.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TradingServiceAsynchTests {

	@Autowired
	private TradingServiceFacade tradingServiceFacade;

	@Autowired
	private AccountProfileService accountProfileService;

	@Autowired
	private AccountService accountService;

	@Autowired
	OrderDataOnDemand orderDataOnDemand;

	@Autowired
	@Qualifier( "rtQuoteService")
	QuoteService quoteService;

	@Autowired
	private Mapper mapper;

	@Test
	public void testService() {
		Accountprofile ap = FallBackAccountProfileService.fakeAccountProfile(true);
		ap = accountProfileService.saveAccountProfile(ap);
		assertNotNull(ap);
		Long accountId = ap.getAccounts().get(0).getAccountid();
		assertNotNull(accountId);

		org.springframework.nanotrader.data.domain.Quote dquote = quoteService.findBySymbol("GOOG");
		Quote quote = new Quote();
		mapper.map(dquote, quote);

		Order order = new Order();
		mapper.map(orderDataOnDemand.getRandomOrder(), order);
		order.setOrdertype("buy");
		order.setAccountid(accountId);
		order.setQuote(quote);
		assertNotNull(order.getAccountid());
		Long i = tradingServiceFacade.saveOrderDirect(order);
		assertNotNull(i);
	}
}
