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
package org.springframework.nanotrader.service.support.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.nanotrader.data.domain.test.HoldingDataOnDemand;
import org.springframework.nanotrader.data.domain.test.OrderDataOnDemand;
import org.springframework.nanotrader.data.service.*;
import org.springframework.nanotrader.service.FallBackAccountProfileService;
import org.springframework.nanotrader.service.FallBackAccountService;
import org.springframework.nanotrader.service.FallBackHoldingService;
import org.springframework.nanotrader.service.FallBackOrderService;
import org.springframework.nanotrader.service.support.TradingServiceFacadeImpl;

/**
 * Java configuration for the application's spring managed beans
 *
 * @author Kashyap Parikh
 * @author Brian Dussault
 */

@Configuration
@ComponentScan(basePackages = "org.springframework.nanotrader.service.support")
public class IntegrationTestConfig {

    @Bean
    public TradingServiceFacadeImpl.OrderGateway orderGateway() {
        return Mockito.mock(TradingServiceFacadeImpl.OrderGateway.class);
    }

    @Bean
    public OrderService orderService() {
        return new FallBackOrderService();
    }

    @Bean
    public HoldingService holdingService() {
        return new FallBackHoldingService();
    }

    @Bean
    public AccountService accountService() {
        return new FallBackAccountService();
    }

    @Bean
    public QuoteService rtQuoteService() {
        return new FallBackQuoteService();
    }

    @Bean
    public AccountProfileService accountProfileService() {
        return new FallBackAccountProfileService();
    }

    @Bean
    OrderDataOnDemand orderDataOnDemand() {
        return new OrderDataOnDemand();
    }

    @Bean
    HoldingDataOnDemand holdingDataOnDemand() {
        return new HoldingDataOnDemand();
    }
}
