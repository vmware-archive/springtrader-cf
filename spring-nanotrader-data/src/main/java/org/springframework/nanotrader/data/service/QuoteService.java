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
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.nanotrader.data.domain.Quote;

public interface QuoteService {

	long countAllQuotes();

	void deleteQuote(Quote quote);

	Quote findQuote(Integer id);

	List<Quote> findAllQuotes();

	Page<Quote> findAllQuotes(PageRequest pageRequest);

	List<Quote> findBySymbolIn(Set<String> symbols);

	List<Quote> findQuoteEntries(int firstResult, int maxResults);

	void saveQuote(Quote quote);

	Quote updateQuote(Quote quote);

	Quote findBySymbol(String symbol);

}
