package org.springframework.nanotrader.data.cloud;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.nanotrader.data.domain.MarketSummary;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.util.NumberUtils;

import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.gson.GsonDecoder;

public class DBQuoteDecoder extends GsonDecoder {

	private static final Logger LOG = Logger.getLogger(DBQuoteDecoder.class);

	@Override
	public Object decode(Response response, Type type) throws IOException,
			DecodeException, FeignException {

		Response.Body body = response.body();
		if (body == null) {
			return null;
		}

		// System.out.println(Util.toString(body.asReader()));

		Type typeOfListOfQuote = new TypeToken<List<Quote>>() {
		}.getType();
		if (Quote.class.equals(type) || typeOfListOfQuote.equals(type)) {
			return processQuoteBody(body);
		}

		if (MarketSummary.class.equals(type)) {
			return processMarketSummaryBody(body);
		}

		return super.decode(response, type);
	}

	private BigDecimal getBigDecimal(ReadContext ctx, String path) {
		Object o = ctx.read(path);
		if (o == null) {
			return new BigDecimal(0);
		}
		return NumberUtils.parseNumber(o.toString(), BigDecimal.class);
	}

	private BigDecimal getBigDecimal(JSONObject jo, String key) {
		if (getString(jo, key) == null) {
			return new BigDecimal(0);
		}
		return new BigDecimal(jo.get(key).toString());
	}

	private Object processQuoteBody(Response.Body body) throws IOException {
		ReadContext ctx = JsonPath.parse(body.asInputStream());

		// might be a list of quotes in a HATEOAS collection
		try {
			return quotesFromJSONArray((JSONArray) ctx
					.read("$._embedded.quotes"));
		} catch (Exception e) {
			LOG.debug("continuing after exception catch.", e);
		}

		// might be a list of raw list of quotes
		if (ctx.json() instanceof JSONArray) {
			return quotesFromJSONArray((JSONArray) ctx.json());
		}

		// or just a single quote
		return quoteFromJson(ctx);
	}

	private MarketSummary processMarketSummaryBody(Response.Body body)
			throws IOException {
		ReadContext ctx = JsonPath.parse(body.asInputStream());
		MarketSummary ms = new MarketSummary();

		ms.setChange(getBigDecimal(ctx, "$.change"));
		ms.setPercentGain(getBigDecimal(ctx, "$.percentGain"));
		ms.setSummaryDate(new Date(getBigDecimal(ctx, "$.summaryDate")
				.longValue()));
		ms.setTradeStockIndexAverage(getBigDecimal(ctx,
				"$.tradeStockIndexAverage"));
		ms.setTradeStockIndexOpenAverage(getBigDecimal(ctx,
				"$.tradeStockIndexOpenAverage"));
		ms.setTradeStockIndexVolume(getBigDecimal(ctx,
				"$.tradeStockIndexVolume"));

		return ms;
	}

	private Quote quoteFromJson(ReadContext ctx) {
		Quote q = new Quote();
		q.setChange1(getBigDecimal(ctx, "$.change"));

		Object name = ctx.read("$.companyname");
		if (name != null) {
			q.setCompanyname(name.toString());
		}

		q.setHigh(getBigDecimal(ctx, "$.high"));
		q.setLow(getBigDecimal(ctx, "$.low"));
		q.setOpen1(getBigDecimal(ctx, "$.open"));
		q.setPrice(getBigDecimal(ctx, "$.price"));
		q.setVolume(getBigDecimal(ctx, "$.volume"));

		if (ctx.read("$._links") != null) {
			Object symbol = getIdFromLink(ctx.read("$._links.self.href")
					.toString());
			if (symbol != null) {
				q.setSymbol(symbol.toString());
			}
		} else {
			q.setSymbol(ctx.read("$.symbol").toString());
		}

		return q;
	}

	private List<Quote> quotesFromJSONArray(JSONArray ja) {
		ArrayList<Quote> quotes = new ArrayList<Quote>();

		for (int i = 0; i < ja.size(); i++) {
			Quote q = new Quote();
			JSONObject jo = (JSONObject) ja.get(i);

			q.setChange1(getBigDecimal(jo, "change"));

			q.setCompanyname(getString(jo, "companyname"));

			q.setHigh(getBigDecimal(jo, "high"));
			q.setLow(getBigDecimal(jo, "low"));
			q.setOpen1(getBigDecimal(jo, "open"));
			q.setPrice(getBigDecimal(jo, "price"));
			q.setVolume(getBigDecimal(jo, "volume"));

			if (jo.get("symbol") != null) {
				q.setSymbol(getString(jo, "symbol"));
			} else {
				q.setSymbol(getIdFromLinks(jo));
			}

			quotes.add(q);
		}

		return quotes;
	}

	private String getIdFromLinks(JSONObject jo) {
		JSONObject links = (JSONObject) jo.get("_links");
		JSONObject self = (JSONObject) links.get("self");
		return getIdFromLink(self.get("href").toString());
	}

	private String getString(JSONObject jo, String key) {
		if (jo == null || key == null || !jo.containsKey(key)) {
			return null;
		}

		return jo.get(key).toString();

	}

	private String getIdFromLink(String link) {
		if (link == null) {
			return "";
		}
		return link.substring(link.lastIndexOf('/') + 1);
	}
}
