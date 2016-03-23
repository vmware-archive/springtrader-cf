package org.springframework.nanotrader.service.support;

import com.netflix.discovery.DiscoveryClient;
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

@Configuration
@ComponentScan(basePackages = "org.springframework.nanotrader.data, org.springframework.nanotrader.service.support, org.springframework.nanotrader.service.cache")
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
    public HoldingService holdingService() {
        return new FallBackHoldingService();
    }

    @Bean
    public FallBackOrderService fallBackOrderService() {
        return new FallBackOrderService();
    }

    @Bean
    public TradingServiceImpl.QuotePublisher publisher() {
        return Mockito.mock(TradingServiceImpl.QuotePublisher.class);
    }

    @Bean
    public TradingServiceFacadeImpl.OrderGateway gateway() {
        return Mockito.mock(TradingServiceFacadeImpl.OrderGateway.class);
    }

    @Bean
    public DiscoveryClient discoveryClient() {
        return Mockito.mock(DiscoveryClient.class);
    }
}
