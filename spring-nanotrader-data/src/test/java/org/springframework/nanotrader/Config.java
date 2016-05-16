package org.springframework.nanotrader;

import com.netflix.discovery.DiscoveryClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.nanotrader.data.service.*;
import org.springframework.nanotrader.service.FallBackAccountProfileService;
import org.springframework.nanotrader.service.FallBackAccountService;
import org.springframework.nanotrader.service.FallBackHoldingService;
import org.springframework.nanotrader.service.FallBackOrderService;

/**
 * Created by jgordon on 3/22/16.
 */

@Configuration
@ComponentScan(basePackages = "org.springframework.nanotrader.data, org.springframework.nanotrader.service")
public class Config {

    @Bean
    public DiscoveryClient discoveryClient() {
        return Mockito.mock(DiscoveryClient.class);
    }

    @Bean
    public String liveQuoteServiceEurekaName() {
        return "foo";
    }

    @Bean
    public QuoteService rtQuoteService() {
        return new FallBackQuoteService();
    }

    @Bean
    public QuoteService dbQuoteService() {
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
    public OrderService orderService() {
        return new FallBackOrderService();
    }

    @Bean
    public TradingServiceImpl.QuotePublisher publisher() {
        return Mockito.mock(TradingServiceImpl.QuotePublisher.class);
    }
}
