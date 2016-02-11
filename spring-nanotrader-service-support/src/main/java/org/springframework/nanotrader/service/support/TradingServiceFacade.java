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

import java.util.Map;

import org.springframework.nanotrader.data.service.TradingService;
import org.springframework.nanotrader.service.domain.Account;
import org.springframework.nanotrader.service.domain.Accountprofile;
import org.springframework.nanotrader.service.domain.CollectionResult;
import org.springframework.nanotrader.service.domain.Holding;
import org.springframework.nanotrader.service.domain.HoldingSummary;
import org.springframework.nanotrader.service.domain.MarketSummary;
import org.springframework.nanotrader.service.domain.Order;
import org.springframework.nanotrader.service.domain.PortfolioSummary;
import org.springframework.nanotrader.service.domain.Quote;

/**
 * @author Gary Russell
 * 
 */
public interface TradingServiceFacade {
	
	Map<String, Object> login(String username, String password);


	void logout(String authtoken);

	Accountprofile findAccountProfile(Long id);
	
	Accountprofile findAccountprofileByUserId(String username);

	Long saveAccountProfile(Accountprofile accountProfileRequest);

	void updateAccountProfile(Accountprofile accountProfileRequest, String username);

	Long saveOrder(Order order, boolean synch);

	Long saveOrderDirect(Order order);
	
	Holding findHolding(Long id, Long accountId);

	CollectionResult findHoldingsByAccountId(Long accountId, Integer page, Integer pageSize);

	Order findOrder(Long orderId, Long accountId);

	void updateOrder(Order orderRequest);

	CollectionResult findOrders(Long accountId, String status, Integer page, Integer pageSize);

	Quote findQuoteBySymbol(String symbol);

	CollectionResult findQuotes();

	Account findAccount(Long id);

	PortfolioSummary findPortfolioSummary(Long accountId);

	MarketSummary findMarketSummary();
	

	Accountprofile findAccountprofileByAuthtoken(String token);
	
	HoldingSummary findHoldingSummary(Long accountId);
	
	void setTradingService(TradingService tradingService);
}
