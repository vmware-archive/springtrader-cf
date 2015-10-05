package org.springframework.nanotrader.data.cloud;

import java.util.List;

import org.springframework.nanotrader.data.domain.MarketSummary;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Repository;

import feign.Param;
import feign.RequestLine;

@Repository
public interface RealTimeQuoteRepository {

	@RequestLine("GET /{symbol}")
	public Quote findBySymbol(@Param(value = "symbol") String symbol);

	@RequestLine("GET /")
	public List<Quote> findAll();

	@RequestLine("GET /symbols")
	List<String> symbols();

	@RequestLine("GET /marketSummary")
	MarketSummary marketSummary();

	@RequestLine("GET /topGainers")
	List<Quote> topGainers();

	@RequestLine("GET /topLosers")
	List<Quote> topLosers();
}
