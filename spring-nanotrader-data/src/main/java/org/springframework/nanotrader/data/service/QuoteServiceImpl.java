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
import org.springframework.nanotrader.data.cloud.QuoteRepository;
import org.springframework.nanotrader.data.domain.MarketSummary;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Service;

@Service
public class QuoteServiceImpl implements QuoteService {

	private static final Logger LOG = Logger.getLogger(QuoteServiceImpl.class);

	@Autowired
    QuoteRepository quoteRepository;

	
	public long countAllQuotes() {
		return quoteRepository.symbols().size();
	}

	public List<Quote> findAllQuotes() {
        return quoteRepository.findAll();
    }

	public List<Quote> topGainers() {
		return quoteRepository.topGainers();
	}

	public List<Quote> topLosers() {
		return quoteRepository.topLosers();
	}

	public Quote findBySymbol(String symbol) {
		if(symbol == null || symbol.length() < 1) {
			return null;
		}
		return quoteRepository.findBySymbol(symbol);
	}

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

	public MarketSummary marketSummary() {
		MarketSummary m = quoteRepository.marketSummary();
		m.setTopGainers(topGainers());
		m.setTopLosers(topLosers());

		return m;
	}

	public void deleteQuote(Quote quote) {
		LOG.info("delete not supported for " + getClass());
	}

	@Override
	public void saveQuote(Quote quote) {
		LOG.info("save not supported for " + getClass());

	}
}
