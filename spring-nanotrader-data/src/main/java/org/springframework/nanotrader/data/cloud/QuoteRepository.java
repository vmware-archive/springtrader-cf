package org.springframework.nanotrader.data.cloud;

import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Repository;

import feign.RequestLine;

@Repository
public interface QuoteRepository {

	@RequestLine("GET /findBySymbol/{symbol}")
	public Quote findBySymbol(@Named("symbol") String symbol);

	@RequestLine("GET /findBySymbolIn?symbols={symbols}")
	public List<Quote> findBySymbolIn(@Named("symbols") Set<String> symbols);
}
