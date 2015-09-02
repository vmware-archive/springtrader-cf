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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.data.repository.QuoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class QuoteServiceImpl implements QuoteService {

	public static final Integer TOP_N = 3;

	@Autowired
    QuoteRepository quoteRepository;

	
	public long countAllQuotes() {
        return quoteRepository.count();
    }

	public void deleteQuote(Quote quote) {
        quoteRepository.delete(quote);
    }

	public Quote findQuote(Integer id) {
        return quoteRepository.findOne(id);
    }

	public List<Quote> findAllQuotes() {
        return quoteRepository.findAll();
    }

	public void saveQuote(Quote quote) {
        quoteRepository.save(quote);
    }

	public Quote findBySymbol(String symbol) {
		return quoteRepository.findBySymbol(symbol);
	}

	@Override
	public List<Quote> findBySymbolIn(Set<String> symbols) {
		if(symbols == null || symbols.size() < 1) {
			return new ArrayList<Quote>();
		}
		return quoteRepository.findBySymbolIn(symbols);
	}

	@Override
	public List<Quote> topGainers() {
		Page<Quote> winners = quoteRepository.findAll(new PageRequest(0, TOP_N, new Sort(Direction.DESC, "change1")));
		List<Quote> l = new ArrayList<Quote>(TOP_N);
		for (Quote q : winners) {
			l.add(q);
		}
		return l;
	}

	@Override
	public List<Quote> topLosers() {
		Page<Quote> losers = quoteRepository.findAll(new PageRequest(0, TOP_N, new Sort(Direction.ASC, "change1")));
		List<Quote> l = new ArrayList<Quote>(TOP_N);
		for (Quote q : losers) {
			l.add(q);
		}
		return l;
	}
}
