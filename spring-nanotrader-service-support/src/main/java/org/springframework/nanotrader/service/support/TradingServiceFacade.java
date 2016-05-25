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


import org.springframework.nanotrader.data.domain.HoldingSummary;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.service.domain.*;

import java.util.Map;

/**
 * @author Gary Russell
 * 
 */
public interface TradingServiceFacade {
	
	Map<String, Object> login(String username, String password);

	void logout(String authtoken);

	Accountprofile findAccountProfile(Long id);

	Long saveAccountProfile(Accountprofile accountProfileRequest);

	void updateAccountProfile(Accountprofile accountProfileRequest, String username);

	Long saveOrder(Order order, boolean synch);

	Long saveOrderDirect(Order order);
	
	Holding findHolding(Long id, Long accountId);

	CollectionResult findHoldingsByAccountId(Long accountId);

	Order findOrder(Long orderId, Long accountId);

	CollectionResult findOrders(Long accountId, String status, Integer page, Integer pageSize);

	Quote findQuoteBySymbol(String symbol);

	CollectionResult findQuotes();

	Account findAccount(Long id);

	Accountprofile findAccountprofileByAuthtoken(String token);
}
