package org.springframework.nanotrader.domain;

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
        String symbol = obj.getSymbol();
        Assert.assertNotNull("Data on demand for 'Quote' failed to provide an identifier", symbol);
        obj = quoteService.findBySymbol(symbol);
        Assert.assertNotNull("Find method for 'Quote' illegally returned null for symbol '" + symbol + "'", obj);
        Assert.assertEquals("Find method for 'Quote' returned the incorrect identifier", symbol, obj.getSymbol());
    }
}
