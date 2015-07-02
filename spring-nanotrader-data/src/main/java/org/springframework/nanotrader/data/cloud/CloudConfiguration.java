package org.springframework.nanotrader.data.cloud;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;

@EnableEurekaClient
@Component
@Profile({ "cloud", "default" })
public class CloudConfiguration {

	@Bean
	QuoteRepository quoteRepository() {
		InstanceInfo i = discoveryClient().getNextServerFromEureka(
				"quote-service", false);
		return new QuoteRepositoryConnectionCreator().createRepository(i
				.getHomePageUrl() + "quoteService");
	}

	DiscoveryClient discoveryClient() {
		DiscoveryManager dm = DiscoveryManager.getInstance();
		dm.initComponent(new MyDataCenterInstanceConfig(),
				new DefaultEurekaClientConfig());

		return dm.getDiscoveryClient();
	}
}
