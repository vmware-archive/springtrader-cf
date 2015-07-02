package org.springframework.nanotrader.data.cloud;

import java.net.URL;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

public class QuoteRepositoryConnectionCreator {

	public QuoteRepository createRepository(String url) {
		QuoteRepository qr = Feign.builder().encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.target(QuoteRepository.class, url);

		return qr;
	}

	public QuoteRepository createRepository(URL url) {
		return createRepository(url.toString());
	}
}
