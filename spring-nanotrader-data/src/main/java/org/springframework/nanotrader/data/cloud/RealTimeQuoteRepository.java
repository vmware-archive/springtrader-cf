package org.springframework.nanotrader.data.cloud;

import java.util.List;
import java.util.Map;

import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Repository;

import feign.Param;
import feign.RequestLine;

@Repository
public interface RealTimeQuoteRepository {

	@RequestLine("GET /findBySymbol/{symbol}")
	public Quote findBySymbol(@Param(value = "symbol") String symbol);

	@RequestLine("GET /findBySymbolIn?symbols={symbols}")
	public List<Quote> findBySymbolIn(@Param(value = "symbols") String symbols);

	@RequestLine("GET /findById/{id}")
	public Quote findQuote(@Param(value = "id") String id);

	@RequestLine("GET /findAll")
	public List<Quote> findAll();

	@RequestLine("GET /count")
	long count();

	@RequestLine("GET /topGainers")
	List<Quote> topGainers();

	@RequestLine("GET /topLosers")
	List<Quote> topLosers();

	@RequestLine("GET /marketSummary")
	Map<String, Float> marketSummary();

	@RequestLine("GET /indexAverage")
	float indexAverage();

	@RequestLine("GET /openAverage")
	float openAverage();

	@RequestLine("GET /volume")
	long volume();

	@RequestLine("GET /change")
	float change();
}
