package org.springframework.nanotrader.data.cloud;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;

@EnableEurekaClient
@Configuration
@Profile({ "cloud", "default" })
public class CloudConfiguration {

	@Bean
	QuoteRepository quoteRepository() {
		String url = discoveryClient().getNextServerFromEureka("quote-service",
				false).getHomePageUrl();
		return new QuoteRepositoryConnectionCreator().createRepository(url
				+ "quoteService");
	}

	@Bean
	DiscoveryClient discoveryClient() {
		DiscoveryManager dm = DiscoveryManager.getInstance();
		dm.initComponent(new MyDataCenterInstanceConfig(),
				new DefaultEurekaClientConfig());

		return dm.getDiscoveryClient();
	}
}
