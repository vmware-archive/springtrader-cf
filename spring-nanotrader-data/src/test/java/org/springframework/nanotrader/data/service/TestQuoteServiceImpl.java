package org.springframework.nanotrader.data.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Service;

@Service
public class TestQuoteServiceImpl implements QuoteService {

	public long countAllQuotes() {
		return 42;
	}

	public Quote findQuote(Integer id) {
		return fakeQuote(id);
	}

	public List<Quote> findAllQuotes() {
		return fakeQuotes(countAllQuotes());
	}

	public List<Quote> findQuoteEntries(int firstResult, int maxResults) {
		return fakeQuotes(maxResults);
	}

	public List<Quote> topGainers() {
		return fakeQuotes(3);
	}

	public List<Quote> topLosers() {
		return fakeQuotes(3);
	}

	public Quote findBySymbol(String symbol) {
		Quote q = fakeQuote(0);
		q.setSymbol(symbol);
		return q;
	}

	public List<Quote> findBySymbolIn(Set<String> symbols) {
		if (symbols == null || symbols.size() < 1) {
			return fakeQuotes(0);
		}
		return fakeQuotes(symbols.size());
	}

	public List<Quote> findRandomQuotes(Integer count) {
		return findAllQuotes().subList(0, count.intValue());
	}

	public Quote saveQuote(Quote quote) {
		if (quote.getQuoteid() == null) {
			quote.setQuoteid(new Integer(0));
		}
		return quote;
	}

	public Map<String, Long> marketSummary() {
		Map<String, Long> m = new HashMap<String, Long>();

		m.put("tradeStockIndexAverage", new Long(123));
		m.put("tradeStockIndexOpenAverage", new Long(234));
		m.put("tradeStockIndexVolume", new Long(345));
		m.put("change", new Long(456));

		return m;
	}

	public void deleteQuote(Quote quote) {
		return;
	}

	private Quote fakeQuote(Integer id) {
		Quote q = new Quote();
		q.setQuoteid(id);
		q.setChange1(new BigDecimal(id));
		q.setCompanyname("Foo" + id);
		q.setHigh(new BigDecimal(id));
		q.setLow(new BigDecimal(id));
		q.setOpen1(new BigDecimal(id));
		q.setPrice(new BigDecimal(id));
		q.setSymbol(q.getCompanyname());
		q.setVolume(new BigDecimal(id));

		return q;
	}

	private List<Quote> fakeQuotes(long size) {
		List<Quote> q = new ArrayList<Quote>();
		for (int i = 0; i < size; i++) {
			q.add(fakeQuote(i));
		}
		return q;
	}
}
