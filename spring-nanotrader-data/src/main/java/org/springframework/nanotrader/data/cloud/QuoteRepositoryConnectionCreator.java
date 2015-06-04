package org.springframework.nanotrader.data.cloud;

import java.net.URL;

import org.springframework.cloud.service.AbstractServiceConnectorCreator;
import org.springframework.cloud.service.ServiceConnectorConfig;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;


public class QuoteRepositoryConnectionCreator extends
		AbstractServiceConnectorCreator<QuoteRepository, WebServiceInfo> {
	
	@Override
	public QuoteRepository create(WebServiceInfo serviceInfo,
			ServiceConnectorConfig serviceConnectorConfig) {
		return createRepository(serviceInfo.getUri());
	}
	
	public QuoteRepository createRepository(String url) {
		
		System.out.println("^^^^^^^^^^^^^$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		
       // ObjectMapper mapper = new ObjectMapper();
//                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//                .registerModule(new Jackson2HalModule());
		

        QuoteRepository qr = Feign.builder()
        		.encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(QuoteRepository.class, url);
        
        System.out.println("*************$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        
        return qr;
    }

    public QuoteRepository createRepository(URL url) {
        return createRepository(url.toString());
    }
}
