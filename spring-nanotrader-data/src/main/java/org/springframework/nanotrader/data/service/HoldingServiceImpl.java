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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.domain.HoldingSummary;
import org.springframework.nanotrader.data.domain.PortfolioSummary;
import org.springframework.nanotrader.data.repository.HoldingAggregateRepository;
import org.springframework.nanotrader.data.repository.HoldingRepository;
import org.springframework.nanotrader.data.repository.PortfolioSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HoldingServiceImpl implements HoldingService {

    @Autowired
    HoldingRepository holdingRepository;

    @Autowired
    HoldingAggregateRepository holdingAggregateRepository;

    @Autowired
    PortfolioSummaryRepository portfolioSummaryRepository;

    public long countByAccountid(Long accountId) {
        return holdingRepository.findCountOfHoldings(accountId);
    }

    public List<Holding> findByAccountid(Long accountId) {
        return holdingRepository.findByAccountAccountid(accountId);
    }

    public Holding findByHoldingidAndAccountid(Long holdingid, Long accountId) {
        return holdingRepository.findByHoldingidAndAccountAccountid(holdingid, accountId);
    }

    public Holding save(Holding holding) {
        return holdingRepository.save(holding);
    }

    public void delete(Holding holding) {
        holdingRepository.delete(holding);
    }

    public Holding find(Long id) {
        return holdingRepository.findOne(id);
    }

    public HoldingSummary findHoldingSummary(Long accountId) {
        return holdingAggregateRepository.findHoldingAggregated(accountId);
    }

    public PortfolioSummary findPortfolioSummary(Long accountId) {
        return portfolioSummaryRepository.findPortfolioSummary(accountId);
    }

    public List<Holding> findAll() {
        return holdingRepository.findAll();
    }
}
