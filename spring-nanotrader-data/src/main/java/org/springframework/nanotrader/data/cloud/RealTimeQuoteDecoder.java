package org.springframework.nanotrader.data.cloud;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minidev.json.JSONArray;

import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.util.NumberUtils;

import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.gson.GsonDecoder;

public class RealTimeQuoteDecoder extends GsonDecoder {

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
			return processBody(body);
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

	private Object processBody(Response.Body body) throws IOException {
		ReadContext ctx = JsonPath.parse(body.asInputStream());

		// is this a single quote, or a collection?
		if (ctx.read("$.").getClass().equals(JSONArray.class)) {
			return quotesFromJson(ctx);
		}

		return quoteFromJson(ctx);
	}

	private Quote quoteFromJson(ReadContext ctx) {
		Quote q = new Quote();
		q.setChange1(getBigDecimal(ctx, "$.Change"));
		q.setCompanyname(ctx.read("$.Name").toString());
		q.setHigh(getBigDecimal(ctx, "$.DaysHigh"));
		q.setLow(getBigDecimal(ctx, "$.DaysLow"));
		q.setOpen1(getBigDecimal(ctx, "$.PreviousClose"));
		q.setPrice(getBigDecimal(ctx, "$.Price"));
		q.setSymbol(ctx.read("$.Symbol").toString());
		q.setVolume(getBigDecimal(ctx, "$.Volume"));

		return q;
	}

	private List<Quote> quotesFromJson(ReadContext ctx) {
		ArrayList<Quote> quotes = new ArrayList<Quote>();

		JSONArray qs = ctx.read("$..");
		for (int i = 0; i < qs.size(); i++) {
			Quote q = new Quote();
			q.setChange1(getBigDecimal(ctx, "$..[" + i + "].Change"));
			q.setCompanyname(ctx.read("$..[" + i + "].Name").toString());
			q.setHigh(getBigDecimal(ctx, "$..[" + i + "].DaysHigh"));
			q.setLow(getBigDecimal(ctx, "$..[" + i + "].DaysLow"));
			q.setOpen1(getBigDecimal(ctx, "$..[" + i + "].PreviousClose"));
			q.setPrice(getBigDecimal(ctx, "$..[" + i + "].Price"));
			q.setSymbol(ctx.read("$..[" + i + "].Symbol").toString());
			q.setVolume(getBigDecimal(ctx, "$..[" + i + "].Volume"));

			quotes.add(q);
		}

		return quotes;
	}

	public static String formatSymbols(Set<String> symbols) {
		if (symbols == null || symbols.size() < 1) {
			return "()";
		}

		Object[] o = symbols.toArray();

		StringBuffer sb = new StringBuffer("(");
		for (int i = 0; i < o.length; i++) {
			sb.append("\'");
			sb.append(o[i]);
			sb.append("\'");
			if (i < o.length - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
