/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.nanotrader.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jgordon
 */
@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
public class QuoteServiceTests {

	@Autowired
	private QuoteService service;

	@Test
	public void testFindBySymbol() {
		Quote q = service.findBySymbol("GOOG");
		assertNotNull(q);
		assertEquals("GOOG", q.getSymbol());
	}
	
	@Test
	public void testFindById() {
		Quote q = service.findQuote(17);
		assertNotNull(q);
		assertEquals(new Integer(17), q.getQuoteid());
	}
	
	@Test
	public void testFindBySymbolIn() {
		Set<String> symbols = new HashSet<String>();
		symbols.add("AAPL");
		symbols.add("GOOG");
		List<Quote> res = service.findBySymbolIn(symbols);
		assertNotNull(res);
		assertTrue(res.size() == 2);
		for (Quote q : res) {
		    assertTrue(q.getSymbol().equals("AAPL") || q.getSymbol().equals("GOOG"));
		}
	}
	
	@Test
	public void testFindAll() {
		List<Quote> quotes = service.findAll();
		assertNotNull(quotes);
		assertTrue(quotes.size() > 0);
	}
	
	@Test
	public void testFindAllPaged() {
		List<Quote> quotes = service.findQuoteEntries(2, 5);
		assertNotNull(quotes);
		assertEquals(5, quotes.size());
	}
	
	@Test
	public void testFindTopMovers() {
		List<Quote> losers = service.findQuoteEntries(new PageRequest(0, 3, new Sort(Direction.ASC, "change1")));
		assertNotNull(losers);
		assertEquals(3, losers.size());
	}
	
	@Test
	public void testMarketSummary() {
		Map<String, Long> ms = service.marketSummary();
		assertNotNull(ms);
		assertEquals(5, ms.size());
	}
}
