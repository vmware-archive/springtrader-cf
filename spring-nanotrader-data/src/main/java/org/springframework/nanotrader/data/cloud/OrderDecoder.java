package org.springframework.nanotrader.data.cloud;

import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import feign.FeignException;
import feign.Response;
import feign.gson.GsonDecoder;
import net.minidev.json.JSONArray;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.domain.Order;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderDecoder extends GsonDecoder {

    private static final Type LIST_OF_ORDERS = new TypeToken<List<Order>>() {
    }.getType();

    private static final Type LIST_OF_HOLDINGS = new TypeToken<List<Holding>>() {
    }.getType();

    private final JsonUtils jsonUtils = new JsonUtils();

    @Override
    public Object decode(Response response, Type type) throws IOException,
            FeignException {

        Response.Body body = response.body();
        if (body == null) {
            return null;
        }

        // System.out.println(Util.toString(body.asReader()));

        if (Order.class.equals(type)) {
            return orderFromJson(JsonPath.parse(body.asInputStream()));
        }

        if (LIST_OF_ORDERS.equals(type)) {
            return ordersFromJson(JsonPath.parse(body.asInputStream()), true);
        }

        if (Holding.class.equals(type)) {
            return holdingFromJson(JsonPath.parse(body.asInputStream()), true);
        }

        if (LIST_OF_HOLDINGS.equals(type)) {
            return holdingsFromJson(JsonPath.parse(body.asInputStream()));
        }

        return super.decode(response, type);
    }

    private Holding holdingFromJson(ReadContext ctx, boolean processOrders) {
        if (ctx.json().toString().length() < 1) {
            return null;
        }

        Holding h = new Holding();
        h.setHoldingid(jsonUtils.getLongValue(ctx, "$.holdingId"));
        h.setPurchaseprice(jsonUtils.getBigDecimalValue(ctx, "$.purchasePrice"));
        h.setQuantity(jsonUtils.getBigDecimalValue(ctx, "$.quantity"));
        h.setPurchasedate(jsonUtils.getDateValue(ctx, "$.purchaseDate"));
        h.setAccountAccountid(jsonUtils.getLongValue(ctx, "$.accountId"));
        h.setQuoteSymbol(jsonUtils.getStringValue(ctx, "$.quoteSymbol"));

        JSONArray orders = ctx.read("$.orders");
        if (orders != null && processOrders) {
            h.setOrders(ordersFromJson(JsonPath.parse(orders.toString()), false));
            for (Order o : h.getOrders()) {
                o.setHoldingHoldingid(h);
            }
        }

        return h;
    }

    private List<Holding> holdingsFromJson(ReadContext ctx) {
        ArrayList<Holding> holdings = new ArrayList<Holding>();

        JSONArray as = ctx.read("$");
        for (int i = 0; i < as.size(); i++) {
            Holding h = new Holding();

            h.setHoldingid(jsonUtils.getLongValue(ctx, "$.[" + i + "].holdingId"));
            h.setPurchaseprice(jsonUtils.getBigDecimalValue(ctx, "$.[" + i + "].purchasePrice"));
            h.setQuantity(jsonUtils.getBigDecimalValue(ctx, "$.[" + i + "].quantity"));
            h.setPurchasedate(jsonUtils.getDateValue(ctx, "$.[" + i + "].purchaseDate"));
            h.setAccountAccountid(jsonUtils.getLongValue(ctx, "$.[" + i + "].accountId"));
            h.setQuoteSymbol(jsonUtils.getStringValue(ctx, "$.[" + i + "].quoteSymbol"));

            holdings.add(h);

            JSONArray orders = ctx.read("$.[" + i + "].orders");
            if (orders != null) {
                h.setOrders(ordersFromJson(JsonPath.parse(orders.toString()), false));
            }
        }

        return holdings;
    }

    private Order orderFromJson(ReadContext ctx) {
        Order o = new Order();

        o.setQuoteid(jsonUtils.getStringValue(ctx, "$.quoteSymbol"));
        o.setQuantity(jsonUtils.getBigDecimalValue(ctx, "$.quantity"));
        o.setPrice(jsonUtils.getBigDecimalValue(ctx, "$.price"));
        o.setAccountid(jsonUtils.getLongValue(ctx, "$.accountId"));
        o.setCompletiondate(jsonUtils.getDateValue(ctx, "$.completionDate"));
        o.setOpendate(jsonUtils.getDateValue(ctx, "$.openDate"));
        o.setOrderfee(jsonUtils.getBigDecimalValue(ctx, "$.orderFee"));
        o.setOrderid(jsonUtils.getLongValue(ctx, "$.orderId"));
        o.setOrderstatus(jsonUtils.getStringValue(ctx, "$.orderStatus"));
        o.setOrdertype(jsonUtils.getStringValue(ctx, "$.orderType"));

        Object h = ctx.read("$.holding");
        if (h != null) {
            o.setHoldingHoldingid(holdingFromJson(JsonPath.parse(h.toString()), false));
        }

        return o;
    }

    private List<Order> ordersFromJson(ReadContext ctx, boolean processHoldings) {
        ArrayList<Order> orders = new ArrayList<Order>();

        JSONArray as = ctx.read("$");
        for (int i = 0; i < as.size(); i++) {
            Order o = new Order();

            o.setQuoteid(jsonUtils.getStringValue(ctx, "$.[" + i + "].quoteSymbol"));
            o.setQuantity(jsonUtils.getBigDecimalValue(ctx, "$.[" + i + "].quantity"));
            o.setPrice(jsonUtils.getBigDecimalValue(ctx, "$.[" + i + "].price"));
            o.setAccountid(jsonUtils.getLongValue(ctx, "$.[" + i + "].accountId"));
            o.setCompletiondate(jsonUtils.getDateValue(ctx, "$.[" + i + "].completionDate"));
            o.setOpendate(jsonUtils.getDateValue(ctx, "$.[" + i + "].openDate"));
            o.setOrderfee(jsonUtils.getBigDecimalValue(ctx, "$.[" + i + "].orderFee"));
            o.setOrderid(jsonUtils.getLongValue(ctx, "$.[" + i + "].orderId"));
            o.setOrderstatus(jsonUtils.getStringValue(ctx, "$.[" + i + "].orderStatus"));
            o.setOrdertype(jsonUtils.getStringValue(ctx, "$.[" + i + "].orderType"));

            Object h = ctx.read("$.[" + i + "].holding");
            if (h != null && processHoldings) {
                o.setHoldingHoldingid(holdingFromJson(JsonPath.parse(h.toString()), false));
            }

            orders.add(o);
        }

        return orders;
    }
}