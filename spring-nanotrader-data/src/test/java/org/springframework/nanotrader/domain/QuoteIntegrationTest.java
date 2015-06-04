package org.springframework.nanotrader.domain;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
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
    QuoteService quoteService;

	@Test
    public void testCountAllQuotes() {
        Assert.assertNotNull("Data on demand for 'Quote' failed to initialize correctly", dod.getRandomQuote());
        long count = quoteService.countAllQuotes();
        Assert.assertTrue("Counter for 'Quote' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindQuote() {
        Quote obj = dod.getRandomQuote();
        Assert.assertNotNull("Data on demand for 'Quote' failed to initialize correctly", obj);
        Integer id = obj.getQuoteid();
        Assert.assertNotNull("Data on demand for 'Quote' failed to provide an identifier", id);
        obj = quoteService.findQuote(id);
        Assert.assertNotNull("Find method for 'Quote' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Quote' returned the incorrect identifier", id, obj.getQuoteid());
    }

	@Test
    public void testFindAllQuotes() {
        Assert.assertNotNull("Data on demand for 'Quote' failed to initialize correctly", dod.getRandomQuote());
        long count = quoteService.countAllQuotes();
        Assert.assertTrue("Too expensive to perform a find all test for 'Quote', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Quote> result = quoteService.findAllQuotes();
        Assert.assertNotNull("Find all method for 'Quote' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Quote' failed to return any data", result.size() > 0);
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
