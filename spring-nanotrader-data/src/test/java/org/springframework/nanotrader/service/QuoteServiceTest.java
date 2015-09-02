package org.springframework.nanotrader.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.data.repository.QuoteRepository;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class QuoteServiceTest {

	@Mock
	private QuoteRepository quoteRepository;

	@InjectMocks
	@Autowired
	QuoteService quoteService;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(quoteRepository.count()).thenReturn(Long.valueOf(22));
		when(quoteRepository.findBySymbol("GOOG"))
				.thenReturn(fakeQuote("GOOG"));
		when(quoteRepository.findAll()).thenReturn(fakeList(22));
		when(quoteRepository.findAll(isA(Pageable.class))).thenReturn(fakePage(3));
		when(quoteRepository.findBySymbolIn(isA(Set.class))).thenReturn(fakeList(3));
	}

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
		assertEquals("GOOG", obj.getCompanyname());
		assertTrue(obj.getHigh().floatValue() > 0.0);
		assertTrue(obj.getLow().floatValue() > 0);
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

		s.add("Foo1");
		res = quoteService.findBySymbolIn(s);
		assertNotNull(res);
//		assertTrue("Should have 1 result.", res.size() == 1);
//		assertTrue("BRCM".equals(res.get(0).getSymbol()));
//
//		s.add("EBAY");
//		res = quoteService.findBySymbolIn(s);
//		assertNotNull(res);
//		assertTrue("Should have 2 results.", res.size() == 2);
//		assertTrue("BRCM".equals(res.get(0).getSymbol())
//				|| "EBAY".equals(res.get(0).getSymbol()));
//		assertTrue("BRCM".equals(res.get(1).getSymbol())
//				|| "EBAY".equals(res.get(1).getSymbol()));
//
//		s.add("YHOO");
//		res = quoteService.findBySymbolIn(s);
//		assertNotNull(res);
//		assertTrue("Should have 3 results.", res.size() == 3);
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
		assertEquals("Foo0", q.getSymbol());
		q = all.get(21);
		assertNotNull(q);
		assertEquals("Foo21", q.getSymbol());
	}

	private Quote fakeQuote(String symbol) {
		Quote q = new Quote();
		q.setChange1(new BigDecimal(1));
		q.setCompanyname(symbol);
		q.setHigh(new BigDecimal(2));
		q.setLow(new BigDecimal(3));
		q.setOpen1(new BigDecimal(4));
		q.setPrice(new BigDecimal(5));
		q.setQuoteid(new Integer(1));
		q.setSymbol(symbol);
		q.setVolume(new BigDecimal(6));

		return q;
	}

	private List<Quote> fakeList(int num) {
		ArrayList<Quote> l = new ArrayList<Quote>();
		for (int i = 0; i < num; i++) {
			l.add(fakeQuote("Foo" + i));
		}
		return l;
	}

	private Page<Quote> fakePage(int num) {
		return new PageImpl<Quote>(fakeList(num));
	}

}
