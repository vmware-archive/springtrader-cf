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

import static org.junit.Assert.assertNotNull;

import org.dozer.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.domain.test.AccountDataOnDemand;
import org.springframework.nanotrader.data.domain.test.OrderDataOnDemand;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.service.domain.Order;
import org.springframework.nanotrader.service.domain.Quote;
import org.springframework.nanotrader.service.support.TradingServiceFacade;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Gary Russell
 *
 */
@ContextConfiguration(locations={
		"classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jpa.xml",
		"classpath:/META-INF/spring/spring-nanotrader-service-support.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class TradingServiceAsynchTests {

	@Autowired
	private TradingServiceFacade tradingServiceFacade;

	@Autowired
	OrderDataOnDemand orderDataOnDemand;

	@Autowired
	AccountDataOnDemand accountDataOnDemand;

	@Autowired
	QuoteService quoteService;

	@Autowired
	private Mapper mapper;

	@Test
	public void testService() {
		Integer accountId = accountDataOnDemand.getRandomAccount().getAccountid();
		org.springframework.nanotrader.data.domain.Quote dquote = quoteService.findBySymbol("GOOG");
		Quote quote = new Quote();
		mapper.map(dquote, quote);

		Order order = new Order();
		mapper.map(orderDataOnDemand.getRandomOrder(), order);
		order.setOrdertype("buy");
		order.setAccountid(accountId);
		order.setQuote(quote);
		assertNotNull(order.getAccountid());
		Integer i = tradingServiceFacade.saveOrderDirect(order);
		assertNotNull(i);
	}

}
