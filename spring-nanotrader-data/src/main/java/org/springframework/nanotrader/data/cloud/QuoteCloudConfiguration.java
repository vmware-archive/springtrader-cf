package org.springframework.nanotrader.data.cloud;

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;

//@Configuration
//@Profile({ "cloud", "default" })
public class QuoteCloudConfiguration extends AbstractCloudConfig {

	@Bean
	public QuoteRepository quoteRepository() {
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		return connectionFactory().service(QuoteRepository.class);
	}

}
