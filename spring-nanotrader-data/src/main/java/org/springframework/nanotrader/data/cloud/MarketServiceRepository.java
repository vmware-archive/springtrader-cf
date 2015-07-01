package org.springframework.nanotrader.data.cloud;

import java.util.List;
import java.util.Map;

import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Repository;

import feign.RequestLine;

@Repository
public interface MarketServiceRepository {

	@RequestLine("GET /count")
	public Integer countAllQuotes();

	@RequestLine("GET /marketSummary")
	public Map<String, Long> marketSummary();

	@RequestLine("GET /topLosers?page=0&size=3")
	public List<Quote> topLosers();

	@RequestLine("GET /topGainers?page=0&size=3")
	public List<Quote> topGainers();
}
