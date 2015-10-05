package org.springframework.nanotrader.data.cloud;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;

@EnableEurekaClient
@EnableAspectJAutoProxy
@Configuration
@Profile("cloud")
public class CloudConfiguration {

	@Bean
	DiscoveryClient discoveryClient() {
		EurekaClientConfigBean configBean = new EurekaClientConfigBean();
		configBean.setRegisterWithEureka(false);
		configBean.setEurekaServerURLContext("standalone-eureka.cfapps.io");

		DiscoveryManager dm = DiscoveryManager.getInstance();
		dm.initComponent(new MyDataCenterInstanceConfig(), configBean);

		return dm.getDiscoveryClient();
	}

	@Bean
	public HystrixCommandAspect hystrixAspect() {
		return new HystrixCommandAspect();
	}
}