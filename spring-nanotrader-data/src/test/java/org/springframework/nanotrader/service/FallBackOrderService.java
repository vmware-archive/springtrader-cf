package org.springframework.nanotrader.service;

import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.service.OrderService;

import java.math.BigDecimal;
import java.util.*;

public class FallBackOrderService implements OrderService {

    static final Map<Long, Order> orders = new HashMap<Long, Order>();

    public Order fakeOrder(Holding holding) {
        Order o = new Order();
        o.setAccountid(-1L);
        o.setHoldingHoldingid(holding);
        o.setOrderstatus("open");
        o.setQuantity(new BigDecimal(123));
        o.setQuoteid("GOOG");
        o.setOpendate(new Date());
        o.setOrderfee(new BigDecimal(23.45));
        o.setOrderid(-1L);
        o.setOrdertype("buy");
        o.setPrice(new BigDecimal(12.34));

        return o;
    }

    @Override
    public Long countAllOrders() {
        return new Long(orders.size());
    }

    @Override
    public Order find(Long id) {
        return orders.get(id);
    }

    @Override
    public List<Order> findAllOrders() {
        return new ArrayList<Order>(orders.values());
    }

    @Override
    public Order saveOrder(Order order) {
        if(order == null) {
            return null;
        }

        if(order.getOrderid() == null) {
            order.setOrderid(UUID.randomUUID().getMostSignificantBits());
        }

        orders.put(order.getOrderid(), order);
        return order;
    }

    @Override
    public Long countOfOrders(Long accountId, String status) {
        return new Long(findOrdersByStatus(accountId, status).size());
    }

    @Override
    public Long countOfOrders(Long accountId) {
        long l = 0;
        for (Order o : orders.values()) {
            if (o.getAccountid().equals(accountId)) {
                l++;
            }
        }
        return l;
    }

    @Override
    public List<Order> findOrdersByStatus(Long accountId, String status) {
        List<Order> ret = new ArrayList<Order>();
        if (accountId == null || status == null) {
            return ret;
        }

        for (Order o : orders.values()) {
            if (o.getAccountid().equals(accountId) && o.getOrderstatus().equals(status)) {
                ret.add(o);
            }
        }
        return ret;
    }

    @Override
    public List<Order> findByAccountId(Long accountId) {
        return null;
    }
}
