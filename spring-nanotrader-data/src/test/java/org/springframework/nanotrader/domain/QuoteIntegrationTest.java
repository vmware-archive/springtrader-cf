package org.springframework.nanotrader.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.data.domain.test.QuoteDataOnDemand;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
public class QuoteIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private QuoteDataOnDemand dod;

	@Autowired
	@Qualifier( "rtQuoteService")
    QuoteService quoteService;

	@Test
    public void testCountAllQuotes() {
        Assert.assertNotNull("Data on demand for 'Quote' failed to initialize correctly", dod.getRandomQuote());
        long count = quoteService.countAllQuotes();
        Assert.assertTrue("Counter for 'Quote' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindQuote() {
        Quote obj = quoteService.findBySymbol("GOOG");
        Assert.assertNotNull("Find method for 'Quote' illegally returned null for id '" + "GOOG" + "'", obj);
        Assert.assertEquals("Find method for 'Quote' returned the incorrect identifier", "GOOG", obj.getQuoteid());
    }

	@Test
    public void testFindAllQuotes() {
        List<Quote> result = quoteService.findAllQuotes();
        Assert.assertNotNull("Find all method for 'Quote' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Quote' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindBySymbolIn() {
		Set<String> symbols = new HashSet<String>();
		symbols.add("GOOG");
        List<Quote> result = quoteService.findBySymbolIn(symbols);
        Assert.assertNotNull("Find by symbol in method for 'Quote' illegally returned null", result);
        Assert.assertTrue("Find by symbol in method for 'Quote' failed to return any data", result.size() > 0);
        Assert.assertTrue("Find by symbol in method for 'Quote' returned wrong number of results", result.size() == 1);

        symbols.add("YHOO");
        result = quoteService.findBySymbolIn(symbols);
        Assert.assertNotNull("Find by symbol in method for 'Quote' illegally returned null", result);
        Assert.assertTrue("Find by symbol in method for 'Quote' failed to return any data", result.size() > 0);
        Assert.assertTrue("Find by symbol in method for 'Quote' returned wrong number of results", result.size() == 2);
    }

	@Test
    public void testSave() {
		Quote quote = new Quote();
		quote.setChange1(new BigDecimal(123));
		quote.setCompanyname("Foo");
		quote.setHigh(new BigDecimal(234));
		quote.setLow(new BigDecimal(345));
		quote.setOpen1(new BigDecimal(456));
		quote.setPrice(new BigDecimal(567));
		quote.setSymbol("FOO" + System.currentTimeMillis());
		quote.setVolume(new BigDecimal(678));
		quote = quoteService.saveQuote(quote);
		Assert.assertNotNull(quote);
		Assert.assertNotNull(quote.getQuoteid());
		quoteService.deleteQuote(quote);
    }
}
