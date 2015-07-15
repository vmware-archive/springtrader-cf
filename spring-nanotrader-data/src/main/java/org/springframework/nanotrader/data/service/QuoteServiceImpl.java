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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.nanotrader.data.cloud.QuoteRepository;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Service;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
@Profile({"default", "cloud"})
public class QuoteServiceImpl implements QuoteService {

	@Autowired
	QuoteRepository quoteRepository;

	@Autowired
	DiscoveryClient discoveryClient;

	private final QuoteService fallBackQuoteService = new FallBackQuoteServiceImpl();

	@HystrixCommand(fallbackMethod = "fallBackCount")
	public long countAllQuotes() {
		return quoteRepository.countAllQuotes();
	}

	@HystrixCommand(fallbackMethod = "fallBackFindQuote")
	public Quote findQuote(Integer id) {
		return quoteRepository.findQuote(id);
	}

	@HystrixCommand(fallbackMethod = "fallBackAllQuotes")
	public List<Quote> findAllQuotes() {
		return quoteRepository.findAll();
	}

	@HystrixCommand(fallbackMethod = "fallBackQuoteEntries")
	public List<Quote> findQuoteEntries(int firstResult, int maxResults) {
		return quoteRepository.findQuoteEntries(firstResult / maxResults,
				maxResults);
	}

	@HystrixCommand(fallbackMethod = "fallBackGainers")
	public List<Quote> topGainers() {
		return quoteRepository.topGainers();
	}

	@HystrixCommand(fallbackMethod = "fallBackLosers")
	public List<Quote> topLosers() {
		return quoteRepository.topLosers();
	}

	@HystrixCommand(fallbackMethod = "fallBackBySymbol")
	public Quote findBySymbol(String symbol) {
		return quoteRepository.findBySymbol(symbol);
	}

	@HystrixCommand(fallbackMethod = "fallBackBySymbols")
	public List<Quote> findBySymbolIn(Set<String> symbols) {
		if(symbols == null || symbols.size() < 1) {
			return new ArrayList<Quote>();
		}
		return quoteRepository.findBySymbolIn(symbols);
	}

	@HystrixCommand(fallbackMethod = "fallBackRandom")
	public List<Quote> findRandomQuotes(Integer count) {
		return findAllQuotes().subList(0, count.intValue());
	}

	@HystrixCommand(fallbackMethod = "fallBackSave")
	public Quote saveQuote(Quote quote) {
		return quoteRepository.save(quote);
	}

	@HystrixCommand(fallbackMethod = "fallBackMarketSummary")
	public Map<String, Long> marketSummary() {
		return quoteRepository.marketSummary();
	}

	@HystrixCommand(fallbackMethod = "fallBackDelete")
	public void deleteQuote(Quote quote) {
		quoteRepository.delete(quote);
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

	public Map<String, Long> fallBackMarketSummary() {
		return fallBackQuoteService.marketSummary();
	}

	public void fallBackDelete(Quote quote) {
		fallBackQuoteService.deleteQuote(quote);
	}
}
