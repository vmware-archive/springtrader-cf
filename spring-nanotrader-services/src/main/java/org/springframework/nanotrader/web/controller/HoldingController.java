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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.nanotrader.data.service.HoldingService;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.service.domain.CollectionResult;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.web.security.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Provides JSON based REST api to Holdings repository
 * 
 * @author Brian Dussault
 */

@Controller
public class HoldingController {

	@Autowired
	private HoldingService holdingService;

	@Autowired
	@Qualifier( "rtQuoteService")
	private QuoteService quoteService;

	@Autowired
	private SecurityUtil securityUtil;

	@RequestMapping(value = "/account/{accountId}/holding/{id}", method = RequestMethod.GET)
	public ResponseEntity<Holding> find(@PathVariable("id") final Long id,
			@PathVariable("accountId") final Long accountId) {

		securityUtil.checkAccount(accountId);

		Holding holding = holdingService.find(id);
		if(holding == null || ! holding.getAccountAccountid().equals(accountId) ) {
			return new ResponseEntity<Holding>(
					BaseController.getNoCacheHeaders(), HttpStatus.NOT_FOUND);
		}

		holding.setQuote(quoteService.findBySymbol(holding.getQuoteSymbol()));

		return new ResponseEntity<Holding>(holding,
				BaseController.getNoCacheHeaders(), HttpStatus.OK);
	}

	@RequestMapping(value = "/account/{accountId}/holdings", method = RequestMethod.GET)
	public ResponseEntity<CollectionResult> findByAccountId(
			@PathVariable("accountId") final Long accountId,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {

		securityUtil.checkAccount(accountId);

		CollectionResult cr = new CollectionResult();
		List<Holding> holdings = holdingService.findByAccountid(accountId);
		if (holdings != null && holdings.size() > 0) {
			for (Holding h : holdings) {
				h.setQuote(quoteService.findBySymbol(h.getQuoteSymbol()));
			}

			cr.setTotalRecords(Long.valueOf("" + holdings.size()));

			if (pageSize != null) {
				cr.setPageSize(pageSize);
			}

			if (page != null) {
				cr.setPage(page);
			}

			cr.setResults(holdings);
		}
		return new ResponseEntity<CollectionResult>(cr,
				BaseController.getNoCacheHeaders(), HttpStatus.OK);

	}

	@RequestMapping(value = "/account/{accountId}/holding", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public void post() {
	}

	@RequestMapping(value = "/account/{accountId}/holding/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public void put() {

	}

	@RequestMapping(value = "/account/{accountId}/holding/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public void delete() {

	}

}
