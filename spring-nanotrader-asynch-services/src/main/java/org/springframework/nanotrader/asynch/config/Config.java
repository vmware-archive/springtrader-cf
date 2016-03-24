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
package org.springframework.nanotrader.asynch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.nanotrader.data.service.DBQuoteService;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.data.service.RealTimeQuoteService;

/**
 * Java configuration for the application's spring managed beans
 *
 * @author Kashyap Parikh
 * @author Brian Dussault
 */

@Configuration
@ComponentScan(basePackages = "org.springframework.nanotrader.service, org.springframework.nanotrader.data, org.springframework.nanotrader.data.service")
@ImportResource({"classpath:/META-INF/spring/integrationContext.xml"})
public class Config {

    @Bean
    public QuoteService rtQuoteService() {
        return new RealTimeQuoteService();
    }

    @Bean
    public QuoteService dbQuoteService() {
        return new DBQuoteService();
    }

}
