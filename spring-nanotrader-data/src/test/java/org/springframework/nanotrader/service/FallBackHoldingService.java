package org.springframework.nanotrader.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.domain.*;
import org.springframework.nanotrader.data.service.HoldingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class FallBackHoldingService implements HoldingService {

    @Autowired
    FallBackOrderService orderService;

    static final Map<Long, Holding> holdings = new HashMap<Long, Holding>();

    public Holding fakeHolding(boolean includeOrder) {
        Holding h = new Holding();

        h.setAccountAccountid(-1L);
        h.setHoldingid(-1L);
        h.setQuantity(new BigDecimal(123));
        h.setQuoteSymbol("GOOG");
        h.setPurchaseprice(new BigDecimal(12.34));
        h.setPurchasedate(new Date());

        if (includeOrder) {
            List<Order> orders = new ArrayList<Order>();
            Order o = orderService.fakeOrder(h);
            orderService.saveOrder(o);
            orders.add(o);
            h.setOrders(orders);
        }

        return h;
    }

    @Override
    public List<Holding> findByAccountid(Long accountId) {
        List<Holding> ret = new ArrayList<Holding>();
        if (accountId == null) {
            return ret;
        }
        for (Holding h : holdings.values()) {
            if (h.getAccountAccountid().equals(accountId)) {
                ret.add(h);
            }
        }
        return ret;
    }

    @Override
    public List<Holding> findAll() {
        return new ArrayList<Holding>(holdings.values());
    }

    @Override
    public Holding save(Holding holding) {
        holdings.put(holding.getHoldingid(), holding);
        return holding;
    }

    @Override
    public void delete(Long id) {
        holdings.remove(id);
    }

    @Override
    public Holding find(Long id) {
        if (id == null) {
            return null;
        }
        return holdings.get(id);
    }

    @Override
    public HoldingSummary findHoldingSummary(Long accountId) {

        HoldingSummary hs = new HoldingSummary();
        hs.setHoldingsTotalGains(new BigDecimal(111.11));

        List<HoldingAggregate> has = new ArrayList<HoldingAggregate>();
        HoldingAggregate ha = new HoldingAggregate();
        ha.setPercent(new BigDecimal(12.34));
        ha.setSymbol("GOOG");
        ha.setGain(new BigDecimal(23.45));
        has.add(ha);

        hs.setHoldingRollups(has);

        return hs;
    }

    @Override
    public PortfolioSummary findPortfolioSummary(Long accountId) {

        PortfolioSummary ps = new PortfolioSummary();
        ps.setTotalBasis(new BigDecimal(123.45));
        ps.setTotalMarketValue(new BigDecimal(234.56));
        ps.setNumberOfHoldings(1);

        return ps;
    }
}
