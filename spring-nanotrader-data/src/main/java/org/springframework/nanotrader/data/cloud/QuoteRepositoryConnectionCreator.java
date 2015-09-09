package org.springframework.nanotrader.data.cloud;

import java.net.URL;

import org.springframework.cloud.service.AbstractServiceConnectorCreator;
import org.springframework.cloud.service.ServiceConnectorConfig;

import feign.Feign;
import feign.gson.GsonEncoder;

public class QuoteRepositoryConnectionCreator extends
		AbstractServiceConnectorCreator<QuoteRepository, WebServiceInfo> {

	@Override
	public QuoteRepository create(WebServiceInfo serviceInfo,
			ServiceConnectorConfig serviceConnectorConfig) {
		return createRepository(serviceInfo.getUri());
	}

	public QuoteRepository createRepository(String url) {
		QuoteRepository qr = Feign.builder().encoder(new GsonEncoder())
				.decoder(new RealTimeQuoteDecoder())
				.target(QuoteRepository.class, url);

		return qr;
	}

	public QuoteRepository createRepository(URL url) {
		return createRepository(url.toString());
	}
}
