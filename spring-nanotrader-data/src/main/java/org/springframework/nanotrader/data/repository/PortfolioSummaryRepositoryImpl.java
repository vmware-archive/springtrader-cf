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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	@PersistenceContext
	private EntityManager em;
	

	@Autowired
	HoldingRepository holdingRepository;

	@Autowired
	@Qualifier( "rtQuoteService")
	QuoteService quoteService;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public PortfolioSummary findPortfolioSummary(Long accountId) {

			PortfolioSummary portfolioSummary = new PortfolioSummary();
			Query query = em.createQuery("SELECT SUM(h.purchaseprice * h.quantity) as purchaseBasis, count(h) FROM Holding h Where h.accountAccountid =:accountId");

			query.setParameter("accountId", accountId);
			@SuppressWarnings("unchecked")
			List<Object[]> result = query.getResultList();
			for (Object[] o: result) {
				BigDecimal price = (BigDecimal)o[0];
				Long countOfHoldings = (Long)o[1];
				portfolioSummary.setTotalBasis(price);
				portfolioSummary.setTotalMarketValue(getTotalMarketValue(accountId));
				portfolioSummary.setNumberOfHoldings(countOfHoldings.intValue());
			}
		return portfolioSummary;
	}

	private BigDecimal getTotalMarketValue(Long accountId) {
		List<Holding> holdings = holdingRepository.findByAccountAccountid(accountId);
		float r = 0;
		for(Holding holding: holdings) {
			Quote quote = quoteService.findBySymbol(holding.getQuoteSymbol());
			float q = holding.getQuantity().floatValue();
			float p = quote.getPrice().floatValue();
			r += (q * p);
		}
		return new BigDecimal(r);
	}

}
