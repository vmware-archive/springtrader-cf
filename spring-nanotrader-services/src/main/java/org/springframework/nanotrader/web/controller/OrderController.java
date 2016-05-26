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
package org.springframework.nanotrader.web.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.service.OrderService;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.data.service.TradingService;
import org.springframework.nanotrader.service.domain.CollectionResult;
import org.springframework.nanotrader.service.support.TradingServiceFacade;
import org.springframework.nanotrader.service.support.exception.NoRecordsFoundException;
import org.springframework.nanotrader.web.security.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides JSON based REST api to Order repository
 * 
 * @author Brian Dussault
 */
@Controller
public class OrderController {

	private static final Logger LOG = Logger.getLogger(OrderController.class);

	@Autowired
	private SecurityUtil securityUtil;

	@Autowired
	private TradingServiceFacade tradingServiceFacade;

	@Autowired
	private OrderService orderService;

	@Autowired
	@Qualifier( "rtQuoteService")
	private QuoteService quoteService;

	@Autowired
	private TradingService tradingService;

	@RequestMapping(value = "/account/{accountId}/orders", method = RequestMethod.GET)
	public ResponseEntity<CollectionResult> findOrders(
			@PathVariable("accountId") final Long accountId,
			@RequestParam(value = "status", required = false) final String status,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {

		securityUtil.checkAccount(accountId);
		return new ResponseEntity<CollectionResult>(getOrders(accountId, status, page, pageSize),
				BaseController.getNoCacheHeaders(), HttpStatus.OK);
	}

	@RequestMapping(value = "/account/{accountId}/order/{id}", method = RequestMethod.GET)
	public ResponseEntity<Order> findOrder(
			@PathVariable("accountId") final Long accountId,
			@PathVariable("id") final Long orderId) {
		securityUtil.checkAccount(accountId);
		Order responseOrder = getOrder(orderId, accountId);

		return new ResponseEntity<Order>(responseOrder, BaseController.getNoCacheHeaders(),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/account/{accountId}/order", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> save(@RequestBody Order orderRequest,
			@PathVariable("accountId") final Long accountId,
			UriComponentsBuilder builder) {

		securityUtil.checkAccount(accountId);
		orderRequest.setAccountid(accountId);
		Long orderId = tradingServiceFacade.saveOrder(orderRequest, true);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(builder.path("/account/" + accountId + "/order/{id}").buildAndExpand(orderId).toUri());

		return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/account/{accountId}/order/asynch", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void saveAsynch(@RequestBody Order orderRequest,
			@PathVariable("accountId") final Long accountId) {

		orderRequest.setAccountid(accountId);
		tradingServiceFacade.saveOrder(orderRequest, false);
	}

	@RequestMapping(value = "/account/{accountId}/order/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public void update(@RequestBody Order orderRequest,
			@PathVariable("accountId") final Integer accountId,
			@PathVariable("id") final Integer orderId) {

	}

	@RequestMapping(value = "/account/{accountId}/order/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public void delete() {

	}

	private Order getOrder(Long orderId, Long accountId) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("findOrder: orderId=" + orderId + " accountId=" + accountId);
		}
		Order order =  orderService.find(orderId);
		if (order == null) {
			throw new NoRecordsFoundException();
		}

		order.setQuote(quoteService.findBySymbol(order.getQuoteid()));
		return order;
	}

	private CollectionResult getOrders(Long accountId, String status, Integer page, Integer pageSize) {
		CollectionResult  collectionResults = new CollectionResult();
		if (LOG.isDebugEnabled()) {
			LOG.debug("findOrders: accountId=" + accountId + " status" + status);
		}
		List<Order> orders;

		collectionResults.setTotalRecords(tradingService.findCountOfOrders(accountId, status));
		collectionResults.setPage(page);
		collectionResults.setPageSize(pageSize);
		if (status != null) {
			orders = tradingService.findOrdersByStatus(accountId, status); //get by status
		} else {
			orders = tradingService.findOrders(accountId); //get all orders
		}

		List<Order> responseOrders = new ArrayList<Order>();
		if (orders != null && orders.size() > 0 ) {


			for(Order o: orders) {
				o.setQuote(quoteService.findBySymbol(o.getQuoteid()));
				responseOrders.add(o);
			}
		}
		collectionResults.setResults(responseOrders);
		return collectionResults;
	}

}
