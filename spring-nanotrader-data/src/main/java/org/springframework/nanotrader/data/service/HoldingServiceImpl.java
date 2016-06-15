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

import com.netflix.discovery.DiscoveryClient;
import feign.Feign;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.nanotrader.data.cloud.HoldingRepository;
import org.springframework.nanotrader.data.cloud.OrderDecoder;
import org.springframework.nanotrader.data.cloud.OrderEncoder;
import org.springframework.nanotrader.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

@Service
@Profile({"default", "cloud"})
public class HoldingServiceImpl implements HoldingService {

    private static final Logger LOG = LogManager.getLogger(HoldingServiceImpl.class);

    private final static int SCALE = 2;
    private final static int ROUND = BigDecimal.ROUND_HALF_UP;
    private final static int TOP_N = 4;

    @Autowired
    private DiscoveryClient discoveryClient;

    private HoldingRepository holdingRepository;

    @Autowired
    private String orderRepositoryName;

    @Autowired
    @Qualifier("realTimeQuoteService")
    private QuoteService quoteService;

    public List<Holding> findByAccountid(Long accountId) {
        return holdingRepository().findByAccountid(accountId);
    }

    public Holding save(Holding holding) {
        return holdingRepository().save(holding);
    }

    public void delete(Long id) {
        holdingRepository().delete(id);
    }

    public Holding find(Long id) {
        return holdingRepository().find(id);
    }

    public HoldingSummary findHoldingSummary(Long accountId) {
        HoldingSummary holdingSummary = new HoldingSummary();
        List<HoldingAggregate> holdingRollups = new ArrayList<HoldingAggregate>();
        List<Holding> holdings = findByAccountid(accountId);

        BigDecimal totalGains = BigDecimal.ZERO;
        totalGains = totalGains.setScale(SCALE, ROUND);

        Map<BigDecimal, HoldingAggregate> hMap = new HashMap<BigDecimal, HoldingAggregate>();
        for (Holding h : holdings) {
            Quote quote = quoteService.findBySymbol(h.getQuoteSymbol());
            BigDecimal gain = quote.getPrice().multiply(h.getQuantity()).subtract(h.getPurchaseprice().multiply(h.getQuantity()));
            gain = gain.setScale(SCALE, ROUND);

            // Filter out the losers (gains =< 0)
            if (gain.floatValue() > 0) {
                totalGains = totalGains.add(gain);
                HoldingAggregate summary = new HoldingAggregate();
                summary.setSymbol(h.getQuoteSymbol());
                summary.setGain(gain);
                hMap.put(gain, summary);
            }
        }

        TreeMap<BigDecimal, HoldingAggregate> sortedMap = new TreeMap<BigDecimal, HoldingAggregate>(hMap);
        for (int i = 0; i < TOP_N; i++) {
            Map.Entry<BigDecimal, HoldingAggregate> e = sortedMap.pollLastEntry();
            if (e != null) {
                holdingRollups.add(e.getValue());
            }
        }

        holdingSummary.setHoldingsTotalGains(totalGains);
        return calculatePercentages(holdingSummary, holdingRollups);
    }

    private HoldingSummary calculatePercentages(HoldingSummary holdingSummary, List<HoldingAggregate> holdingRollups) {
        double hundredPercent = 100;
        BigDecimal gainsRemainder = holdingSummary.getHoldingsTotalGains();
        for (HoldingAggregate ha : holdingRollups) {
            BigDecimal percent = calculateGainPercentage(ha.getGain(), holdingSummary.getHoldingsTotalGains());
            ha.setPercent(percent);
            hundredPercent = hundredPercent - ha.getPercent().doubleValue();
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

    public PortfolioSummary findPortfolioSummary(Long accountId) {
        PortfolioSummary portfolioSummary = new PortfolioSummary();

        //get all of the holdings for this account
        List<Holding> holdings = findByAccountid(accountId);
        int count = holdings.size();
        BigDecimal basis = BigDecimal.ZERO;
        for (Holding h : holdings) {
            basis = basis.add(h.getPurchaseprice().multiply(h.getQuantity()));
        }

        portfolioSummary.setTotalBasis(basis);
        portfolioSummary.setTotalMarketValue(getTotalMarketValue(accountId));
        portfolioSummary.setNumberOfHoldings(count);

        return portfolioSummary;
    }

    private BigDecimal getTotalMarketValue(Long accountId) {
        List<Holding> holdings = findByAccountid(accountId);
        float r = 0;
        for (Holding holding : holdings) {
            Quote quote = quoteService.findBySymbol(holding.getQuoteSymbol());
            float q = holding.getQuantity().floatValue();
            float p = quote.getPrice().floatValue();
            r += (q * p);
        }
        return new BigDecimal(r);
    }

    private BigDecimal calculateGainPercentage(BigDecimal gain, BigDecimal totalGains) {
        BigDecimal percent = gain.divide(totalGains, 4, RoundingMode.HALF_UP);
        percent = percent.multiply(BigDecimal.valueOf(100),
                new MathContext(4, RoundingMode.HALF_UP));
        return percent;
    }

    public List<Holding> findAll() {
        return holdingRepository.findAll();
    }

    private HoldingRepository holdingRepository() {
        if (this.holdingRepository == null) {
            LOG.info("initializing holdingRepository named: " + orderRepositoryName);
            String url = discoveryClient.getNextServerFromEureka(
                    orderRepositoryName, false).getHomePageUrl();

            LOG.info("holdingRepository url is: " + url);

            this.holdingRepository = Feign.builder()
                    .encoder(new OrderEncoder())
                    .decoder(new OrderDecoder())
                    .target(HoldingRepository.class, url);

            LOG.info("holdingRepository initialization complete.");
        }
        return this.holdingRepository;
    }
}
