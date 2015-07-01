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
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.domain.MarketSummary;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.data.util.FinancialUtils;
import org.springframework.stereotype.Repository;

/**
 * @author Brian Dussault
 *
 */
@Repository
public class MarketSummaryRepositoryImpl implements MarketSummaryRepository {
	@PersistenceContext
	private EntityManager em;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	@Autowired 
	QuoteService quoteService;

	public MarketSummary findMarketSummary() {
		MarketSummary marketSummary = new MarketSummary();
		Map<String, Long> ms = quoteService.marketSummary();

		marketSummary.setTradeStockIndexAverage(BigDecimal.ZERO.setScale(FinancialUtils.SCALE, FinancialUtils.ROUND));
		marketSummary.setTradeStockIndexOpenAverage(BigDecimal.ZERO.setScale(FinancialUtils.SCALE, FinancialUtils.ROUND));
		marketSummary.setTradeStockIndexVolume(BigDecimal.ZERO.setScale(FinancialUtils.SCALE, FinancialUtils.ROUND));
		marketSummary.setChange(BigDecimal.ZERO.setScale(FinancialUtils.SCALE, FinancialUtils.ROUND));

		marketSummary.setTradeStockIndexAverage(new BigDecimal(ms.get("tradeStockIndexAverage")));

		marketSummary.setTradeStockIndexOpenAverage(new BigDecimal(ms.get("tradeStockIndexOpenAverage")));

		marketSummary.setTradeStockIndexVolume(new BigDecimal(ms.get("tradeStockIndexVolume")));
		marketSummary.setChange(new BigDecimal(ms.get("change")));

		return marketSummary;
	}
}
