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
package org.springframework.nanotrader.asynch.service;

import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.nanotrader.data.service.*;
import org.springframework.nanotrader.service.FallBackAccountProfileService;
import org.springframework.nanotrader.service.FallBackAccountService;
import org.springframework.nanotrader.service.FallBackHoldingService;
import org.springframework.nanotrader.service.FallBackOrderService;

/**
 * Java configuration for the application's spring managed beans
 *
 * @author Kashyap Parikh
 * @author Brian Dussault
 */

@Configuration
@ComponentScan(basePackages = "org.springframework.nanotrader.service.support, org.springframework.nanotrader.data.service, org.springframework.nanotrader.service.cache")
public class Config {

    @Bean
    public DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean() throws Exception {
        DozerBeanMapperFactoryBean d = new DozerBeanMapperFactoryBean();
        d.setMappingFiles(new Resource[]{new ClassPathResource("dozer-bean-mappings.xml")});
        return d;
    }

    @Bean
    public QuoteService rtQuoteService() {
        return new FallBackQuoteService();
    }


    @Bean
    public AccountService accountService() {
        return new FallBackAccountService();
    }

    @Bean
    public AccountProfileService accountProfileService() {
        return new FallBackAccountProfileService();
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
    public TradingServiceImpl.QuotePublisher publisher() {
        return Mockito.mock(TradingServiceImpl.QuotePublisher.class);
    }
}
