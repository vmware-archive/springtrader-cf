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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.data.service.AccountService;
import org.springframework.nanotrader.data.service.HoldingService;
import org.springframework.nanotrader.data.service.OrderService;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.data.util.FinancialUtils;
import org.springframework.stereotype.Service;

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
public class TradingServiceImpl implements TradingService {

	private static Logger log = Logger.getLogger(TradingServiceImpl.class);

	@Autowired
	private OrderService orderService;

	@Autowired
	private HoldingService holdingService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private QuoteService realTimeQuoteService;

	@Override
	public Order saveOrder(Order order)  {
		Order createdOrder;
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
		Quote quote = realTimeQuoteService.findBySymbol(order.getQuoteid());
		// create order and persist
		Order createdOrder;

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
		Holding holding = holdingService.find(order.getHoldingid());
		if (holding == null) {
			throw new RuntimeException("Attempted to sell holding"
					+ order.getHoldingHoldingid().getHoldingid() + " which is already sold.");
		}
		Quote quote = realTimeQuoteService.findBySymbol(holding.getQuoteSymbol());
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

			
//		updateQuoteMarketData(order.getQuoteid(), FinancialUtils.getRandomPriceChangeFactor(), order.getQuantity());
	
		
		return order;
	}

	// TODO: Need to clean this up
	private void updateAccount(Order order, Quote quote, Account account) {
		// update account balance
		BigDecimal price = quote.getPrice();
		BigDecimal orderFee = order.getOrderfee();
		BigDecimal balance = account.getBalance();
		BigDecimal total;
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

	public static interface QuotePublisher {
		void publishQuote(Quote quote);
	}

	//@Transactional
	public void updateQuote(Quote quote) {
		realTimeQuoteService.saveQuote(quote);
	}
}
