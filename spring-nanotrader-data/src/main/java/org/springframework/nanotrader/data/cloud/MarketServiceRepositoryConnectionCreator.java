package org.springframework.nanotrader.data.cloud;

import java.net.URL;

import org.springframework.cloud.service.AbstractServiceConnectorCreator;
import org.springframework.cloud.service.ServiceConnectorConfig;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

public class MarketServiceRepositoryConnectionCreator
		extends
		AbstractServiceConnectorCreator<MarketServiceRepository, WebServiceInfo> {

	@Override
	public MarketServiceRepository create(WebServiceInfo serviceInfo,
			ServiceConnectorConfig serviceConnectorConfig) {
		return createRepository(serviceInfo.getUri());
	}

	public MarketServiceRepository createRepository(String url) {
		MarketServiceRepository qr = Feign.builder()
				.encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
				.target(MarketServiceRepository.class, url);

		return qr;
	}

	public MarketServiceRepository createRepository(URL url) {
		return createRepository(url.toString());
	}
}
