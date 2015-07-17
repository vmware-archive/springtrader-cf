package org.springframework.nanotrader.data.cloud;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;

import feign.Feign;
import feign.gson.GsonEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

@EnableEurekaClient
@EnableAspectJAutoProxy
@Configuration
@Profile({ "cloud", "default" })
public class CloudConfiguration {

	@Bean
	DBQuoteRepository quoteRepository() {
		String url = discoveryClient().getNextServerFromEureka(
				"db-quote-service", false).getHomePageUrl();

		return Feign.builder().encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.target(DBQuoteRepository.class, url + "quoteService");
	}

	@Bean
	RealTimeQuoteRepository realTimeQuoteRepository() {
		String url = discoveryClient().getNextServerFromEureka(
				"real-time-quote-service", false).getHomePageUrl();

		return Feign.builder().encoder(new GsonEncoder())
				.decoder(new RealTimeQuoteDecoder())
				.target(RealTimeQuoteRepository.class, url + "quoteService");
	}

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

}
