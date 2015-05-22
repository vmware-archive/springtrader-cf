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
package org.springframework.nanotrader.data.repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.domain.PortfolioSummary;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.stereotype.Repository;

/**
 * @author Brian Dussault
 *
 */
@Repository
public class PortfolioSummaryRepositoryImpl implements PortfolioSummaryRepository {

	@Autowired
	HoldingRepository holdingRepository;

	@Autowired
	QuoteService quoteClient;

	@PersistenceContext
	private EntityManager em;
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public PortfolioSummary findPortfolioSummary(Integer accountId) {
		PortfolioSummary portfolioSummary = new PortfolioSummary();
		List<Holding> holdings = holdingRepository
				.findByAccountAccountid(accountId);

		Map<String, Quote> quotes = getQuotesFromHoldings(holdings);

		portfolioSummary.setTotalBasis(getTotalBasis(holdings));
		portfolioSummary.setTotalMarketValue(getTotalMarketValue(holdings, quotes));
		portfolioSummary.setNumberOfHoldings(holdings.size());

		return portfolioSummary;
	}

	private Map<String, Quote> getQuotesFromHoldings(List<Holding> holdings) {
		Set<String> symbols = new HashSet<String>();
		for(Holding h: holdings) {
			symbols.add(h.getQuoteSymbol());
		}
		List<Quote> quotes = quoteClient.findBySymbolIn(symbols);
		Map<String, Quote> ret = new HashMap<String, Quote>();
		for(Quote q: quotes) {
			ret.put(q.getSymbol(), q);
		}

		return ret;
	}

	private BigDecimal getTotalBasis(List<Holding> holdings) {
		BigDecimal ret = new BigDecimal(0);
		for(Holding h: holdings) {
			ret.add(h.getPurchaseprice().multiply(h.getQuantity()));
		}
		return ret;
	}

	private BigDecimal getTotalMarketValue(List<Holding> holdings, Map<String, Quote> quotes) {
		BigDecimal ret = new BigDecimal(0);
		for(Holding h: holdings) {
			ret.add(h.getQuantity().multiply(quotes.get(h.getQuoteSymbol()).getPrice()));
		}
		return ret;
	}
}
