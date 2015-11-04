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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.nanotrader.data.cloud.DBQuoteDecoder;
import org.springframework.nanotrader.data.cloud.DBQuoteRepository;
import org.springframework.nanotrader.data.domain.MarketSummary;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Service;

import com.netflix.discovery.DiscoveryClient;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import feign.Feign;
import feign.gson.GsonEncoder;

@Service
@Profile("cloud")
public class DBQuoteService implements QuoteService {

	private static final Logger LOG = Logger.getLogger(DBQuoteService.class);

	private DBQuoteRepository dbQuoteRepository;

	@Autowired
	DiscoveryClient discoveryClient;

	@Autowired
	String dbQuoteServiceEurekaName;

	private final QuoteService fallBackQuoteService = new FallBackQuoteService();

	@HystrixCommand(fallbackMethod = "fallBackCount")
	public long countAllQuotes() {
		return quoteRepository().count();
	}

	@HystrixCommand(fallbackMethod = "fallBackAllQuotes")
	public List<Quote> findAllQuotes() {
		return quoteRepository().findAll();
	}

	@HystrixCommand(fallbackMethod = "fallBackGainers")
	public List<Quote> topGainers() {
		return quoteRepository().topGainers();
	}

	@HystrixCommand(fallbackMethod = "fallBackLosers")
	public List<Quote> topLosers() {
		return quoteRepository().topLosers();
	}

	@HystrixCommand(fallbackMethod = "fallBackBySymbol")
	public Quote findBySymbol(String symbol) {
		return quoteRepository().findBySymbol(symbol);
	}

	@HystrixCommand(fallbackMethod = "fallBackBySymbols")
	public List<Quote> findBySymbolIn(Set<String> symbols) {
		ArrayList<Quote> ret = new ArrayList<Quote>();

		if (symbols == null || symbols.size() < 1) {
			return ret;
		}

		List<Quote> all = findAllQuotes();
		for (Quote q : all) {
			if (symbols.contains(q.getSymbol())) {
				ret.add(q);
			}
		}

		return ret;
	}

	@HystrixCommand(fallbackMethod = "fallBackSave")
	public Quote saveQuote(Quote quote) {
		return quoteRepository().save(quote);
	}

	@HystrixCommand(fallbackMethod = "fallBackMarketSummary")
	public MarketSummary marketSummary() {
		MarketSummary ms = quoteRepository().marketSummary();
		ms.setTopGainers(topGainers());
		ms.setTopLosers(topLosers());

		return ms;
	}

	@HystrixCommand(fallbackMethod = "fallBackDelete")
	public void deleteQuote(Quote quote) {
		quoteRepository().delete(quote);
	}

	public long fallBackCount() {
		return fallBackQuoteService.countAllQuotes();
	}

	public List<Quote> fallBackAllQuotes() {
		return fallBackQuoteService.findAllQuotes();
	}

	public List<Quote> fallBackGainers() {
		return fallBackQuoteService.topGainers();
	}

	public List<Quote> fallBackLosers() {
		return fallBackQuoteService.topLosers();
	}

	public Quote fallBackBySymbol(String symbol) {
		return fallBackQuoteService.findBySymbol(symbol);
	}

	public List<Quote> fallBackBySymbols(Set<String> symbols) {
		return fallBackQuoteService.findBySymbolIn(symbols);
	}

	public Quote fallBackSave(Quote quote) {
		return fallBackQuoteService.saveQuote(quote);
	}

	public MarketSummary fallBackMarketSummary() {
		return fallBackQuoteService.marketSummary();
	}

	public void fallBackDelete(Quote quote) {
		fallBackQuoteService.deleteQuote(quote);
	}

	private DBQuoteRepository quoteRepository() {
		if (this.dbQuoteRepository == null) {
			LOG.info("initializing db-quote-repository.");
			String url = discoveryClient.getNextServerFromEureka(
					dbQuoteServiceEurekaName, false).getHomePageUrl()
					+ "quotes";

			LOG.info("db-quote-repository url is: " + url);

			this.dbQuoteRepository = Feign.builder().encoder(new GsonEncoder())
					.decoder(new DBQuoteDecoder())
					.target(DBQuoteRepository.class, url);

			LOG.info("db-quote-repository initialization complete.");
		}
		return this.dbQuoteRepository;
	}
}