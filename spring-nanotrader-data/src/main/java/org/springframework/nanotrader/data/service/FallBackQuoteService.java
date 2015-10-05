package org.springframework.nanotrader.data.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.nanotrader.data.domain.MarketSummary;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Service;

@Service
public class FallBackQuoteService implements QuoteService {

	private static final Random RANDOM = new Random();

	public long countAllQuotes() {
		return 42;
	}

	public Quote findQuote(String id) {
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
		Quote q = fakeQuote(symbol);
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
		return quote;
	}

	public MarketSummary marketSummary() {
		MarketSummary m = new MarketSummary();

		m.setTradeStockIndexAverage(new BigDecimal(123.45));
		m.setTradeStockIndexOpenAverage(new BigDecimal(234.56));
		m.setTradeStockIndexVolume(new BigDecimal(345.67));
		m.setChange(new BigDecimal(456.78));
		m.setPercentGain(new BigDecimal(1.23));
		m.setTopGainers(topGainers());
		m.setTopLosers(topLosers());
		m.setSummaryDate(new Date());

		return m;
	}

	public void deleteQuote(Quote quote) {
		return;
	}

	private Quote fakeQuote(String id) {
		int i = RANDOM.nextInt(100);

		Quote q = new Quote();
		q.setCompanyname(id);
		q.setChange1(new BigDecimal(RANDOM.nextInt(25)));
		q.setHigh(q.getChange1().add(new BigDecimal(i)));
		q.setLow(new BigDecimal(i - 15));
		q.setOpen1(new BigDecimal(i));
		q.setPrice(new BigDecimal(i + 5));
		q.setSymbol(id);
		q.setVolume(new BigDecimal(id.hashCode()));

		return q;
	}

	private List<Quote> fakeQuotes(long size) {
		List<Quote> q = new ArrayList<Quote>();
		for (int i = 0; i < size; i++) {
			q.add(fakeQuote("Foo" + i));
		}
		return q;
	}
}