package org.springframework.nanotrader.data.cloud;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Repository;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Repository
public interface DBQuoteRepository {

	@RequestLine("GET /findBySymbol/{symbol}")
	public Quote findBySymbol(@Param("symbol") String symbol);

	@RequestLine("GET /findById/{id}")
	public Quote findQuote(@Param("id") Integer id);

	@RequestLine("GET /findAll")
	public List<Quote> findAll();

	@RequestLine("GET /findBySymbolIn?symbols={symbols}")
	public List<Quote> findBySymbolIn(@Param("symbols") Set<String> symbols);

	@RequestLine("GET /findAllPaged?page={from}&size={to}")
	public List<Quote> findQuoteEntries(@Param("from") int from,
			@Param("to") int to);

	@RequestLine("GET /findAllPaged?page={from}&size={to}&sort={column},{direction}")
	public List<Quote> findQuoteEntries(@Param("page") PageRequest page);

	@RequestLine("GET /count")
	public Integer countAllQuotes();

	@RequestLine("GET /marketSummary")
	public Map<String, Float> marketSummary();
	
	@RequestLine("GET /topLosers?page=0&size=3")
	public List<Quote> topLosers();
	
	@RequestLine("GET /topGainers?page=0&size=3")
	public List<Quote> topGainers();
	
	@RequestLine("POST /save")
	@Headers("Content-Type: application/json")
	public Quote save(Quote quote);
	
	@RequestLine("POST /delete")
	@Headers("Content-Type: application/json")
	public void delete(Quote quote);
}
