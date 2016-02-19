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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.domain.HoldingAggregate;
import org.springframework.nanotrader.data.domain.HoldingSummary;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.data.service.HoldingService;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.data.util.FinancialUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

@Repository
public class HoldingAggregateRepositoryImpl implements HoldingAggregateRepository {
	private static int TOP_N = 4;
	@PersistenceContext
	private EntityManager em;

	@Autowired
	@Qualifier( "rtQuoteService")
	QuoteService quoteService;

	@Autowired
	HoldingService holdingService;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public HoldingSummary findHoldingAggregated(Long accountId) {
	
		HoldingSummary holdingSummary = new HoldingSummary();
		List<HoldingAggregate> holdingRollups = new ArrayList<HoldingAggregate>();
		List<Holding> holdings = holdingService.findByAccountid(accountId);

		BigDecimal totalGains = BigDecimal.ZERO;
		totalGains = totalGains.setScale(FinancialUtils.SCALE, FinancialUtils.ROUND);

		Map<BigDecimal, HoldingAggregate> hMap = new HashMap<BigDecimal, HoldingAggregate>();
		for (Holding h : holdings) {
			Quote quote = quoteService.findBySymbol(h.getQuoteSymbol());
			BigDecimal gain = quote.getPrice().multiply(h.getQuantity()).subtract(h.getPurchaseprice().multiply(h.getQuantity()));
			gain.setScale(FinancialUtils.SCALE, FinancialUtils.ROUND);

			// Filter out the losers (gains =< 0)
			if(gain.floatValue() > 0) {
				totalGains = totalGains.add(gain);
				HoldingAggregate summary = new HoldingAggregate();
				summary.setSymbol(h.getQuoteSymbol());
				summary.setGain(gain);
				hMap.put(gain, summary);
			}
		}

		TreeMap<BigDecimal, HoldingAggregate> sortedMap = new TreeMap<BigDecimal, HoldingAggregate>(hMap);
		for(int i = 0; i < TOP_N;i++) {
			Entry<BigDecimal, HoldingAggregate> e = sortedMap.pollLastEntry();
			if(e != null) {
				holdingRollups.add(e.getValue());
			}
		}

		holdingSummary.setHoldingsTotalGains(totalGains);
		HoldingSummary summary = calculatePercentages(holdingSummary, holdingRollups);
		return summary;
	}
	
	private HoldingSummary calculatePercentages(HoldingSummary holdingSummary, List<HoldingAggregate> holdingRollups) { 
		double hundredPercent = 100;
		BigDecimal gainsRemainder = holdingSummary.getHoldingsTotalGains();
		for (HoldingAggregate ha: holdingRollups) { 
			BigDecimal percent = FinancialUtils.calculateGainPercentage(ha.getGain(), holdingSummary.getHoldingsTotalGains());
			ha.setPercent(percent);
			hundredPercent = hundredPercent- ha.getPercent().doubleValue();
			gainsRemainder = gainsRemainder.subtract(ha.getGain());
			
		}
		// Since we are only showing the Top N symbols, lump all others into the Other bucket
		// at this point if we are still @ 100%, then no records were found
		// also trying to prevent Other showing simply due to rounding (hence  > 1) 
		if (hundredPercent > 1 && hundredPercent != 100) {
			HoldingAggregate summary = new HoldingAggregate();
			summary.setSymbol("Other");
			summary.setPercent(BigDecimal.valueOf(hundredPercent));
			summary.setGain(gainsRemainder);
			holdingRollups.add(summary);
		}
		holdingSummary.setHoldingRollups(holdingRollups);
		
		return holdingSummary;
	}
}
