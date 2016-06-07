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
package org.springframework.nanotrader.web.configuration;

import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = {"org.springframework.nanotrader.data.service, org.springframework.nanotrader.service.support"})
@ImportResource({"classpath:/META-INF/spring/integration/amqp-outbound-context.xml", "classpath:/META-INF/spring/integration/amqp-data-outbound-context.xml"})
@Profile("cloud")
public class ServiceConfig {

    @Bean
    DiscoveryClient discoveryClient() {
        DiscoveryManager dm = DiscoveryManager.getInstance();
        dm.initComponent(new MyDataCenterInstanceConfig(),
                new DefaultEurekaClientConfig());

        return dm.getDiscoveryClient();
    }

    @Bean
    public HystrixCommandAspect hystrixAspect() {
        return new HystrixCommandAspect();
    }

    @Autowired
    Environment env;

    @Bean
    public String liveQuoteServiceEurekaName() {
        return env.getProperty("LIVE_QUOTE_SERVICE_NAME");
    }

    @Bean
    public String dbQuoteServiceEurekaName() {
        return env.getProperty("DB_QUOTE_SERVICE_NAME");
    }


    @Bean
    public String accountRepositoryName() {
        return env.getProperty("ACCOUNT_SERVICE_NAME");
    }

    @Bean
    public String orderRepositoryName() {
        return env.getProperty("ORDER_SERVICE_NAME");
    }
}
