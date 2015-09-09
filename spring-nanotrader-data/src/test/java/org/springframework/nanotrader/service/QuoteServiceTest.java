package org.springframework.nanotrader.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.nanotrader.data.domain.MarketSummary;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Profile("test")
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class QuoteServiceTest {

	@Autowired
	QuoteService quoteService;

	@Test
	public void testFindBySymbol() {
		Quote obj = quoteService.findBySymbol("GOOG");
		assertNotNull("Should find a result.", obj);

		// change can be positive, negative or zero, so just make sure nothing
		// is thrown
		try {
			obj.getChange1();
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		assertEquals("Google Inc.", obj.getCompanyname());
		assertTrue(obj.getOpen1().floatValue() > 0);
		assertTrue(obj.getPrice().floatValue() > 0);
		assertEquals("GOOG", obj.getSymbol());
		assertTrue(obj.getVolume().floatValue() > 0);
		assertNotNull(obj.getQuoteid());
	}

	@Test
	public void testFindBySymbolIn() {
		List<Quote> res = quoteService.findBySymbolIn(null);
		assertNotNull(res);
		assertTrue("Should have no results.", res.size() == 0);

		Set<String> s = new HashSet<String>();
		res = quoteService.findBySymbolIn(s);
		assertNotNull(res);
		assertTrue("Should have no results.", res.size() == 0);

		s.add("GOOG");
		res = quoteService.findBySymbolIn(s);
		assertNotNull(res);
		assertTrue("Should have 1 result.", res.size() == 1);
		assertTrue("GOOG".equals(res.get(0).getSymbol()));

		s.add("EBAY");
		res = quoteService.findBySymbolIn(s);
		assertNotNull(res);
		assertTrue("Should have 2 results.", res.size() == 2);
		assertTrue("GOOG".equals(res.get(0).getSymbol())
				|| "EBAY".equals(res.get(0).getSymbol()));
		assertTrue("GOOG".equals(res.get(1).getSymbol())
				|| "EBAY".equals(res.get(1).getSymbol()));

		s.add("YHOO");
		res = quoteService.findBySymbolIn(s);
		assertNotNull(res);
		assertTrue("Should have 3 results.", res.size() == 3);
	}

	@Test
	public void testGainers() {
		List<Quote> qs = quoteService.topGainers();
		assertNotNull(qs);
		assertEquals(3, qs.size());
		for (Quote q : qs) {
			assertNotNull(q);
			assertNotNull(q.getSymbol());
			assertNotNull(q.getChange1());
		}
	}

	@Test
	public void testLosers() {
		List<Quote> qs = quoteService.topLosers();
		assertNotNull(qs);
		assertEquals(3, qs.size());
		for (Quote q : qs) {
			assertNotNull(q);
			assertNotNull(q.getSymbol());
			assertNotNull(q.getChange1());
		}
	}

	@Test
	public void testCountAllQuotes() {
		assertEquals("22", "" + quoteService.countAllQuotes());
	}

	@Test
	public void testFindAll() {
		List<Quote> all = quoteService.findAllQuotes();
		assertEquals("22", "" + all.size());
		Quote q = all.get(0);
		assertNotNull(q);
		assertNotNull(q.getSymbol());
		q = all.get(21);
		assertNotNull(q);
		assertNotNull(q.getSymbol());
	}

	@Test
	public void testMarketSummary() {
		MarketSummary m = quoteService.marketSummary();
		assertNotNull(m);
		assertNotNull(m.getChange());
		assertNotNull(m.getPercentGain());
		assertNotNull(m.getSummaryDate());
		assertNotNull(m.getTopGainers());
		assertNotNull(m.getTopLosers());
		assertNotNull(m.getTradeStockIndexAverage());
		assertNotNull(m.getTradeStockIndexOpenAverage());
		assertNotNull(m.getTradeStockIndexVolume());
	}

}
