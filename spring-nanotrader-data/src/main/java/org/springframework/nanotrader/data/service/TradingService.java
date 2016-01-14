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
package org.springframework.nanotrader.data.service;

import java.util.List;
import java.util.Set;

import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.domain.HoldingSummary;
import org.springframework.nanotrader.data.domain.MarketSummary;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.domain.PortfolioSummary;
import org.springframework.nanotrader.data.domain.Quote;

/**
 * @author Brian Dussault
 * @author Gary Russell
 *
 */
public interface TradingService {

	public static final String ORDER_TYPE_BUY = "buy";

	public static final String ORDER_TYPE_SELL = "sell";
	
	public abstract Accountprofile login(String username, String password);
	
	public abstract void logout(String authtoken);
	
	public abstract Accountprofile findAccountProfile(Long id);
	
	public abstract Accountprofile findAccountByUserId(String id);

	public abstract Accountprofile saveAccountProfile(Accountprofile accountProfile);

	public abstract Accountprofile updateAccountProfile(Accountprofile accountProfile, String username);

	public abstract Holding findHolding(Integer id, Long accountId);

	public abstract Holding updateHolding(Holding holding);

	public abstract void saveHolding(Holding holding);

	public abstract Order findOrder(Integer id, Long accountId);

	public abstract Order saveOrder(Order order);

	public abstract Order updateOrder(Order order);
	
	public abstract Long findCountOfOrders(Long accountId, String status);

	public abstract List<Order> findOrdersByStatus(Long accountId, String status, Integer page, Integer pageSize);

	public abstract List<Order> findOrders(Long accountId, Integer page, Integer pageSize);

	public abstract List<Holding> findHoldingsByAccountId(Long accountId, Integer page, Integer pageSize);
	
	public abstract List<Quote> findRandomQuotes(Integer count);

	public abstract List<Quote> findAllQuotes();

	public abstract Quote findQuoteBySymbol(String symbol);

	public abstract List<Quote> findQuotesBySymbols(Set<String> symbols);

	public abstract Account findAccount(Long accountId);
	
	public abstract Account findAccountByProfile(Accountprofile accountProfile);

	public abstract PortfolioSummary findPortfolioSummary(Long accountId);
	
	public abstract MarketSummary findMarketSummary();

	public abstract Accountprofile findByAuthtoken(String token);
	
	public abstract HoldingSummary findHoldingSummary(Long accountId);

	public Long findCountOfHoldingsByAccountId(Long accountId);
	
	public abstract void deleteAll();
	
	public abstract void deleteAccountByUserid(String userId);
	
	public void updateQuote(Quote quote);
}