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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.nanotrader.data.cloud.RealTimeQuoteDecoder;
import org.springframework.nanotrader.data.cloud.RealTimeQuoteRepository;
import org.springframework.nanotrader.data.cloud.ScheduledUpdatable;
import org.springframework.nanotrader.data.domain.MarketSummary;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Service;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import feign.Feign;
import feign.gson.GsonEncoder;

@Service
@Profile("cloud")
public class RealTimeQuoteService implements QuoteService, ScheduledUpdatable {

	private static final Logger LOG = Logger
			.getLogger(RealTimeQuoteService.class);

	private RealTimeQuoteRepository realTimeQuoteRepository;

	@Autowired
	DiscoveryClient discoveryClient;

	@Autowired
	@Qualifier("dbQuoteService")
	QuoteService dbQuoteService;

	@HystrixCommand(fallbackMethod = "fallBackCount")
	public long countAllQuotes() {
		return realTimeQuoteRepository().symbols().size();
	}

	@HystrixCommand(fallbackMethod = "fallBackAllQuotes")
	public List<Quote> findAllQuotes() {
		return findAllQuotesDirect();
	}

	/**
	 * call this method directly to bypass fallback.
	 */
	private List<Quote> findAllQuotesDirect() {
		return realTimeQuoteRepository().findAll();
	}

	@HystrixCommand(fallbackMethod = "fallBackGainers")
	public List<Quote> topGainers() {
		return realTimeQuoteRepository().topGainers();
	}

	@HystrixCommand(fallbackMethod = "fallBackLosers")
	public List<Quote> topLosers() {
		return realTimeQuoteRepository().topLosers();
	}

	@HystrixCommand(fallbackMethod = "fallBackBySymbol")
	public Quote findBySymbol(String symbol) {
		return realTimeQuoteRepository().findBySymbol(symbol);
	}

	@HystrixCommand(fallbackMethod = "fallBackBySymbols")
	public List<Quote> findBySymbolIn(Set<String> symbols) {
		if (symbols == null || symbols.size() < 1) {
			return new ArrayList<Quote>();
		}

		List<Quote> ret = new ArrayList<Quote>();
		List<Quote> all = realTimeQuoteRepository.findAll();
		for (Quote q : all) {
			if (symbols.contains(q.getSymbol())) {
				ret.add(q);
			}
		}
		return ret;
	}

	@HystrixCommand(fallbackMethod = "fallBackSave")
	public Quote saveQuote(Quote quote) {
		LOG.info("save not supported for " + getClass());
		return quote;
	}

	@HystrixCommand(fallbackMethod = "fallBackMarketSummary")
	public MarketSummary marketSummary() {
		return realTimeQuoteRepository().marketSummary();
	}

	@HystrixCommand(fallbackMethod = "fallBackDelete")
	public void deleteQuote(Quote quote) {
		LOG.info("delete not supported for " + getClass());
	}

	public long fallBackCount() {
		return dbQuoteService.countAllQuotes();
	}

	public List<Quote> fallBackAllQuotes() {
		return dbQuoteService.findAllQuotes();
	}

	public List<Quote> fallBackGainers() {
		return dbQuoteService.topGainers();
	}

	public List<Quote> fallBackLosers() {
		return dbQuoteService.topLosers();
	}

	public Quote fallBackBySymbol(String symbol) {
		return dbQuoteService.findBySymbol(symbol);
	}

	public List<Quote> fallBackBySymbols(Set<String> symbols) {
		return dbQuoteService.findBySymbolIn(symbols);
	}

	public Quote fallBackSave(Quote quote) {
		return dbQuoteService.saveQuote(quote);
	}

	public MarketSummary fallBackMarketSummary() {
		return dbQuoteService.marketSummary();
	}

	public void fallBackDelete(Quote quote) {
		dbQuoteService.deleteQuote(quote);
	}

	private RealTimeQuoteRepository realTimeQuoteRepository() {
		if (this.realTimeQuoteRepository == null) {
			String url = discoveryClient.getNextServerFromEureka(
					"real-time-quote-service", false).getHomePageUrl();

			this.realTimeQuoteRepository = Feign
					.builder()
					.encoder(new GsonEncoder())
					.decoder(new RealTimeQuoteDecoder())
					.target(RealTimeQuoteRepository.class, url + "quoteService");
		}
		return this.realTimeQuoteRepository;
	}

	// @Scheduled(fixedDelay = 6000)
	public void updateValues() {
		LOG.info("Updating fallback service quotes.");
		try {
			List<Quote> all = findAllQuotesDirect();
			for (Quote quote : all) {
				dbQuoteService.saveQuote(quote);
			}
		} catch (Throwable t) {
			LOG.error("Error updating fallback service quotes.", t);
		}
	}
}
