package org.springframework.nanotrader.data.cloud;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import feign.RequestTemplate;
import feign.gson.GsonEncoder;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.domain.Order;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderEncoder extends GsonEncoder {

    private static final Type MAP_OF_STRING_OBJECT = new TypeToken<Map<String, Object>>() {
    }.getType();

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private final Gson gson = new Gson();

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) {

        Object o = null;
        if (bodyType.equals(Holding.class)) {
            o = processHolding((Holding) object, true);
        }

        if (bodyType.equals(Order.class)) {
            o = processOrder((Order) object, true);
        }

        template.body(gson.toJson(o, MAP_OF_STRING_OBJECT));
    }

    private Object processHolding(Holding h, boolean processOrders) {
        if (h == null) {
            return null;
        }

        Map<String, Object> m = new HashMap<String, Object>();

        m.put("holdingId", h.getHoldingid());
        m.put("quantity", h.getQuantity());
        m.put("accountId", h.getAccountAccountid());

        if (h.getPurchasedate() != null) {
            m.put("purchaseDate", DATE_FORMAT.format(h.getPurchasedate()));
        }

        m.put("purchasePrice", h.getPurchaseprice());
        m.put("quoteSymbol", h.getQuoteSymbol());

        if (h.getOrders() != null && h.getOrders().size() > 0 && processOrders) {
            List<Object> l = new ArrayList<Object>();
            for (Order o : h.getOrders()) {
                l.add(processOrder(o, false));
            }
            m.put("orders", l);
        }

        return m;
    }

    private Object processOrder(Order o, boolean processHolding) {
        Map<String, Object> m = new HashMap<String, Object>();

        m.put("orderId", o.getOrderid());
        m.put("accountId", o.getAccountid());
        m.put("quantity", o.getQuantity());
        m.put("quoteSymbol", o.getQuoteid());

        if (o.getOpendate() != null) {
            m.put("openDate", DATE_FORMAT.format(o.getOpendate()));
        }

        if (o.getCompletiondate() != null) {
            m.put("completionDate", DATE_FORMAT.format(o.getCompletiondate()));
        }

        m.put("price", o.getPrice());
        m.put("orderFee", o.getOrderfee());
        m.put("orderStatus", o.getOrderstatus());
        m.put("orderType", o.getOrdertype());

        if (o.getHoldingHoldingid() != null && processHolding) {
            m.put("holding",
                    processHolding(o.getHoldingHoldingid(), false));
        }

        return m;
    }
}