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
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.nanotrader.data.cloud.RealTimeQuoteDecoder;
import org.springframework.nanotrader.data.cloud.RealTimeQuoteRepository;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Service;

import com.netflix.discovery.DiscoveryClient;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
@Profile({"default", "cloud"})
public class RealTimeQuoteService implements QuoteService {
	
	private static final Logger LOG = Logger.getLogger(RealTimeQuoteService.class);

	@Autowired
	@Qualifier("realTimeQuoteRepository")
	RealTimeQuoteRepository realTimeQuoteRepository;

	@Autowired
	DiscoveryClient discoveryClient;

	private final QuoteService fallBackQuoteService = new DBQuoteService();

//	@HystrixCommand(fallbackMethod = "fallBackCount")
	public long countAllQuotes() {
		return realTimeQuoteRepository.count();
	}

//	@HystrixCommand(fallbackMethod = "fallBackFindQuote")
	public Quote findQuote(Integer id) {
		return realTimeQuoteRepository.findQuote(id);
	}

//	@HystrixCommand(fallbackMethod = "fallBackAllQuotes")
	public List<Quote> findAllQuotes() {
		return realTimeQuoteRepository.findAll();
	}

//	@HystrixCommand(fallbackMethod = "fallBackQuoteEntries")
	public List<Quote> findQuoteEntries(int firstResult, int maxResults) {
		long allQuotesSize = countAllQuotes();

		if (firstResult < 0) {
			throw new IllegalArgumentException(
					"firstResult must be greater than -1");
		}

		if (firstResult >= allQuotesSize) {
			throw new IllegalArgumentException(
					"firstResult must be less than the size of all quotes");
		}

		if (maxResults < 1) {
			throw new IllegalArgumentException(
					"maxResults must be greater than 0");
		}

		if (maxResults >= allQuotesSize) {
			throw new IllegalArgumentException(
					"maxResults can't be greater than the size of all quotes");
		}

		if (maxResults - firstResult < 1) {
			throw new IllegalArgumentException(
					"maxResults must be greater than firstResult");
		}

		// I think we're ready ....
		List<Quote> all = realTimeQuoteRepository.findAll();
		return all.subList(firstResult, maxResults);
	}

//	@HystrixCommand(fallbackMethod = "fallBackGainers")
	public List<Quote> topGainers() {
		return realTimeQuoteRepository.topGainers();
	}

//	@HystrixCommand(fallbackMethod = "fallBackLosers")
	public List<Quote> topLosers() {
		return realTimeQuoteRepository.topLosers();
	}

//	@HystrixCommand(fallbackMethod = "fallBackBySymbol")
	public Quote findBySymbol(String symbol) {
		return realTimeQuoteRepository.findBySymbol(symbol);
	}

//	@HystrixCommand(fallbackMethod = "fallBackBySymbols")
	public List<Quote> findBySymbolIn(Set<String> symbols) {
		if (symbols == null || symbols.size() < 1) {
			return new ArrayList<Quote>();
		}
		return realTimeQuoteRepository.findBySymbolIn(RealTimeQuoteDecoder
				.formatSymbols(symbols));
	}

//	@HystrixCommand(fallbackMethod = "fallBackRandom")
	public List<Quote> findRandomQuotes(Integer count) {
		return findAllQuotes().subList(0, count.intValue());
	}

//	@HystrixCommand(fallbackMethod = "fallBackSave")
	public Quote saveQuote(Quote quote) {
		LOG.info("save not supported for " + getClass());
		return quote;
	}

//	@HystrixCommand(fallbackMethod = "fallBackMarketSummary")
	public Map<String, Float> marketSummary() {
		return realTimeQuoteRepository.marketSummary();
	}

//	@HystrixCommand(fallbackMethod = "fallBackDelete")
	public void deleteQuote(Quote quote) {
		LOG.info("delete not supported for " + getClass());
	}

	public long fallBackCount() {
		return fallBackQuoteService.countAllQuotes();
	}

	public Quote fallBackFindQuote(Integer id) {
		return fallBackQuoteService.findQuote(id);
	}

	public List<Quote> fallBackAllQuotes() {
		return fallBackQuoteService.findAllQuotes();
	}

	public List<Quote> fallBackQuoteEntries(int firstResult, int maxResults) {
		return fallBackQuoteService.findQuoteEntries(firstResult, maxResults);
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

	public List<Quote> fallBackRandom(Integer count) {
		return fallBackQuoteService.findRandomQuotes(count);
	}

	public Quote fallBackSave(Quote quote) {
		return fallBackQuoteService.saveQuote(quote);
	}

	public Map<String, Float> fallBackMarketSummary() {
		return fallBackQuoteService.marketSummary();
	}

	public void fallBackDelete(Quote quote) {
		fallBackQuoteService.deleteQuote(quote);
	}
}
