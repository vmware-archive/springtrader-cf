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
import org.springframework.nanotrader.data.cloud.QuoteRepository;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Service;

@Service
public class QuoteServiceImpl implements QuoteService {

	@Autowired
	QuoteRepository quoteRepository;

	public long countAllQuotes() {
		return quoteRepository.countAllQuotes();
	}

	public Quote findQuote(Integer id) {
		return quoteRepository.findQuote(id);
	}

	public List<Quote> findAllQuotes() {
		return quoteRepository.findAll();
	}

	public List<Quote> findQuoteEntries(int firstResult, int maxResults) {
		return quoteRepository.findQuoteEntries(firstResult / maxResults,
				maxResults);
	}

	public List<Quote> topGainers() {
		return quoteRepository.topGainers();
	}

	public List<Quote> topLosers() {
		return quoteRepository.topLosers();
	}

	public Quote findBySymbol(String symbol) {
		return quoteRepository.findBySymbol(symbol);
	}

	public List<Quote> findBySymbolIn(Set<String> symbols) {
		if(symbols == null || symbols.size() < 1) {
			return new ArrayList<Quote>();
		}
		return quoteRepository.findBySymbolIn(symbols);
	}

	public List<Quote> findRandomQuotes(Integer count) {
		return findAllQuotes().subList(0, count.intValue());
	}

	public Quote saveQuote(Quote quote) {
		return quoteRepository.save(quote);
	}

	public Map<String, Long> marketSummary() {
		return quoteRepository.marketSummary();
	}

	public void deleteQuote(Quote quote) {
		quoteRepository.delete(quote);
	}
}
