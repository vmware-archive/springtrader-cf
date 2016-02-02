package org.springframework.nanotrader.data.cloud;

import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@EnableEurekaClient
@EnableAspectJAutoProxy
@Configuration
@Profile("cloud")
public class CloudConfiguration {

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
}