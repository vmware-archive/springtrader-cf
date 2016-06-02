package org.springframework.nanotrader.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.nanotrader.data.domain.test.HoldingDataOnDemand;
import org.springframework.nanotrader.data.domain.test.OrderDataOnDemand;
import org.springframework.nanotrader.data.service.*;

@Configuration
public class Config {

    @Bean
    public QuoteService quoteService() {
        return new FallBackQuoteService();
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
    public FallBackHoldingService holdingService() {
        return new FallBackHoldingService();
    }

    @Bean
    public FallBackOrderService fallBackOrderService() {
        return new FallBackOrderService();
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