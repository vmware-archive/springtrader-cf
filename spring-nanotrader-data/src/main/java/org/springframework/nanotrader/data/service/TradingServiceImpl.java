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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.data.util.FinancialUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Brian Dussault
 * @author Gary Russell
 *
 */

@Service
@Transactional
public class TradingServiceImpl implements TradingService {

	private static Logger log = LoggerFactory.getLogger(TradingServiceImpl.class);

	public static BigDecimal DEFAULT_ORDER_FEE = BigDecimal.valueOf(1050, 2);

	private static String OPEN_STATUS = "open";

	private static String CANCELLED_STATUS = "cancelled";

	@Autowired
	private AccountProfileService accountProfileService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private HoldingService holdingService;

	@Autowired
	private AccountService accountService;

	@Autowired
	@Qualifier( "rtQuoteService")
	private QuoteService quoteService;

	@Autowired
	QuotePublisher quotePublisher;

	@Override
	@Transactional 
	public Order saveOrder(Order order)  {
		Order createdOrder = null;
		if (log.isDebugEnabled()) {
			log.debug("TradingServices.saveOrder: order=" + order.toString());
		}
		if (ORDER_TYPE_BUY.equals(order.getOrdertype())) {
			createdOrder = buy(order);
		} else if (ORDER_TYPE_SELL.equals(order.getOrdertype())) {
			createdOrder = sell(order);
		} else {
			throw new UnsupportedOperationException(
					"Order type was not recognized. Valid order types are 'buy' or 'sell'");
		}
		
		if (log.isDebugEnabled()) {
			log.debug("TradingServices.saveOrder: completed successfully.");
		}
		
		
		return createdOrder;
	}

	private Order buy(Order order) {
		
		Account account = accountService.findAccount(order.getAccountid());
		Quote quote = quoteService.findBySymbol(order.getQuoteid());
		// create order and persist
		Order createdOrder = null;

		if(quote == null) {
			throw new RuntimeException("null quote");
		}

		if ((order.getQuantity() != null && order.getQuantity().intValue() > 0)
				&& (account.getBalance().subtract(order.getQuantity().multiply(quote.getPrice())).doubleValue() >= 0)) { // cannot
																															// buy
			createdOrder = createOrder(order, account, null, quote);
			// Update account balance and create holding
			completeOrder(createdOrder, quote, account);
		}
		else {
			order.setQuantity(new BigDecimal(0));
			createdOrder = createOrder(order, account, null, quote);
			// cancel order
			createdOrder.setCompletiondate(new Date());
			createdOrder.setOrderstatus(CANCELLED_STATUS);
		}

		return orderService.saveOrder(createdOrder);
	}

	private Order sell(Order order) {
		Account account = accountService.findAccount(order.getAccountid());
		Holding holding = holdingService.find(order.getHoldingHoldingid().getHoldingid());
		if (holding == null) {
			throw new DataRetrievalFailureException("Attempted to sell holding"
					+ order.getHoldingHoldingid().getHoldingid() + " which is already sold.");
		}
		Quote quote = quoteService.findBySymbol(holding.getQuoteSymbol());
		// create order and persist
		
		Order createdOrder = createOrder(order, account, holding, quote);
		// Update account balance and create holding
		completeOrder(createdOrder, quote, account);
		return orderService.saveOrder(createdOrder);
	}

	private Order createOrder(Order order, Account account, Holding holding, Quote quote) {
		order.setAccountid(account.getAccountid());
		order.setQuoteid(quote.getSymbol());
		if (order.getQuantity() == null) {
			order.setQuantity(holding.getQuantity());
		}
		order.setOrderfee(DEFAULT_ORDER_FEE);
		order.setOrderstatus(OPEN_STATUS);
		order.setOpendate(new Date());
		order.setPrice(quote.getPrice().setScale(FinancialUtils.SCALE, FinancialUtils.ROUND));
		order.setHoldingHoldingid(holding);
		return order;
	}

	// TO DO: refactor this
	private Order completeOrder(Order order, Quote quote, Account account) {
		if (ORDER_TYPE_BUY.equals(order.getOrdertype())) {
			if (order.getHoldingHoldingid() == null) {
				Holding holding = new Holding();
				holding.setAccountAccountid(order.getAccountid());
				holding.setPurchasedate(new Date());
				holding.setQuantity(order.getQuantity());
				holding.setPurchaseprice(order.getPrice());
				holding.setQuoteSymbol(order.getQuoteid());
				List<Order> orders = new ArrayList<Order>();
				orders.add(order);
				holding.setOrders(orders);
				order.setHoldingHoldingid(holding);
				updateAccount(order, quote, account);
			}
		}
		else {
			updateAccount(order, quote, account);
		}
		order.setOrderstatus("completed");
		order.setCompletiondate(new Date());

			
		updateQuoteMarketData(order.getQuoteid(), FinancialUtils.getRandomPriceChangeFactor(), order.getQuantity());
	
		
		return order;
	}

	// TODO: Need to clean this up
	private void updateAccount(Order order, Quote quote, Account account) {
		// update account balance
		BigDecimal price = quote.getPrice();
		BigDecimal orderFee = order.getOrderfee();
		BigDecimal balance = account.getBalance();
		BigDecimal total = null;
		if (ORDER_TYPE_BUY.equals(order.getOrdertype())) {
			total = (order.getQuantity().multiply(price)).add(orderFee);
			account.setBalance(balance.subtract(total));
		}
		else {
			total = (order.getQuantity().multiply(price)).subtract(orderFee);
			account.setBalance(balance.add(total));
			List<Order> orders = order.getHoldingHoldingid().getOrders();
			// Remove the holding id from the buy record
			for (Order orderToDeleteHolding : orders) {
				orderToDeleteHolding.setHoldingHoldingid(null);
			}
			// remove the holding id from the sell record
			Holding holding = order.getHoldingHoldingid();
			order.setHoldingHoldingid(null);
			holdingService.delete(holding.getHoldingid());
		}
		accountService.saveAccount(account);
	}

	public void updateQuoteMarketData(String symbol, BigDecimal changeFactor, BigDecimal sharesTraded) {
			Quote quote = quoteService.findBySymbol(symbol);
			Quote quoteToPublish = new Quote();
			quoteToPublish.setCompanyname(quote.getCompanyname());
			quoteToPublish.setSymbol(quote.getSymbol());
			quoteToPublish.setOpen1(quote.getOpen1());
			BigDecimal oldPrice = quote.getPrice();
			if (quote.getPrice().compareTo(FinancialUtils.PENNY_STOCK_PRICE) <= 0) {
				changeFactor = FinancialUtils.PENNY_STOCK_RECOVERY_MIRACLE_MULTIPLIER;
			}
			if (quote.getPrice().compareTo(quote.getLow()) <= 0) { 
				quoteToPublish.setLow(quote.getPrice());
			} else { 
				quoteToPublish.setLow(quote.getLow());
			}
			
			if (quote.getPrice().compareTo(quote.getHigh()) > 0) { 
				quoteToPublish.setHigh(quote.getPrice());
			} else { 
				quoteToPublish.setHigh(quote.getHigh());
			}
			
			BigDecimal newPrice = changeFactor.multiply(oldPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
			quoteToPublish.setPrice(newPrice);
			quoteToPublish.setVolume(quote.getVolume().add(sharesTraded));
			quoteToPublish.setChange1(newPrice.subtract(quote.getOpen1()));
			this.quotePublisher.publishQuote(quoteToPublish);
	}
	
	@Transactional
	public void updateQuote(Quote quote) {
		quoteService.saveQuote(quote);
	}

	@Override
	public Long findCountOfOrders(Long accountId, String status) {
		Long countOfOrders = null;
		if (log.isDebugEnabled()) {
			log.debug("TradingServices.findCountOfHoldings: accountId=" + accountId + " status=" + status);
		}
		if (status != null) {
			countOfOrders = orderService.countOfOrders(accountId, status);
		}
		else {
			countOfOrders = orderService.countOfOrders(accountId);
		}

		if (log.isDebugEnabled()) {
			log.debug("TradingServices.findCountOfHoldings: completed successfully.");
		}
		return countOfOrders;
	}

	@Override
	public List<Order> findOrdersByStatus(Long accountId, String status) {
		List<Order> orders = null;

		if (log.isDebugEnabled()) {
			log.debug("TradingServices.findOrdersByStatus: accountId=" + accountId + " status=" + status);
		}
		
		orders = orderService.findOrdersByStatus(accountId, status);
		if (log.isDebugEnabled()) {
			log.debug("TradingServices.findOrdersByStatus: completed successfully.");
		}

		return orders;
	}

	@Override
	@Transactional
	public List<Order> findOrders(Long accountId) {
		List<Order> orders = null;
		if (log.isDebugEnabled()) {
			log.debug("TradingServices.findOrders: accountId=" + accountId);
		}
		orders = orderService.findByAccountId(accountId);

		if (log.isDebugEnabled()) {
			log.debug("TradingServices.findOrders: completed successfully.");
		}

		return orders;
	}

	public static interface QuotePublisher {

		void publishQuote(Quote quote);
	}
}
