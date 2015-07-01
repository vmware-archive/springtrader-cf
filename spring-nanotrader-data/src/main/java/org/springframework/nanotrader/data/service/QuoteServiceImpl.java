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
import org.springframework.nanotrader.data.cloud.MarketServiceRepository;
import org.springframework.nanotrader.data.cloud.QuoteRepository;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Service;

@Service
public class QuoteServiceImpl implements QuoteService {

	@Autowired
	QuoteRepository quoteRepository;

	@Autowired
	MarketServiceRepository marketServiceRepository;

	public long countAllQuotes() {
		return marketServiceRepository.countAllQuotes();
	}

	public List<Quote> topGainers() {
		return marketServiceRepository.topGainers();
	}

	public List<Quote> topLosers() {
		return marketServiceRepository.topLosers();
	}

	public Quote findBySymbol(String symbol) {
		return quoteRepository.findBySymbol(symbol);
	}

	public List<Quote> findBySymbolIn(Set<String> symbols) {
		if (symbols == null || symbols.size() < 1) {
			return new ArrayList<Quote>();
		}
		return quoteRepository.findBySymbolIn(symbols);
	}

	public Map<String, Long> marketSummary() {
		return marketServiceRepository.marketSummary();
	}
}
