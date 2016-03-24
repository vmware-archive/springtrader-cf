package org.springframework.nanotrader.web.configuration;

import org.springframework.context.annotation.*;
import org.springframework.nanotrader.data.service.DBQuoteService;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.data.service.RealTimeQuoteService;

/**
 * Created by jgordon on 3/22/16.
 */

@Configuration
@Profile("cloud")
@ComponentScan(basePackages = "org.springframework.nanotrader.data, org.springframework.nanotrader.data.service")
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
