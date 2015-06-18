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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.nanotrader.data.domain.Quote;

public interface QuoteService {

	public long countAllQuotes();

	public Quote findQuote(Integer id);

	public List<Quote> findAllQuotes();

	public List<Quote> findQuoteEntries(int firstResult, int maxResults);

	public Quote findBySymbol(String symbol);

	public List<Quote> findBySymbolIn(Set<String> symbols);

	public List<Quote> findRandomQuotes(Integer count);

	public Quote saveQuote(Quote quote);

	public Map<String, Long> marketSummary();

	public List<Quote> topGainers();

	public List<Quote> topLosers();

	public void deleteQuote(Quote quote);
}
