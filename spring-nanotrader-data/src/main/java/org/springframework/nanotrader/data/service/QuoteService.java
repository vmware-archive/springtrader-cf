package org.springframework.nanotrader.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QuoteService {

	private String serviceURI;

	private final RestTemplate restTemplate = new RestTemplate();

	public QuoteService(String serviceURI) {
		super();
		setServiceURI(serviceURI);
	}

	public Quote findBySymbol(String symbol) {
		return restTemplate.getForObject(
				serviceURI + "/findBySymbol/" + symbol, Quote.class);
	}

	public Quote findQuote(Integer id) {
		return restTemplate.getForObject(serviceURI + "/findById/" + id,
				Quote.class);
	}

	public List<Quote> findBySymbolIn(Set<String> symbols) {
		Quote[] quotes = restTemplate.getForObject(serviceURI
				+ "/findBySymbolIn?symbols=" + setToQueryList(symbols),
				Quote[].class);
		return new ArrayList<Quote>(Arrays.asList(quotes));

	}

	public List<Quote> findAll() {
		Quote[] quotes = restTemplate.getForObject(serviceURI + "/findAll/",
				Quote[].class);
		return new ArrayList<Quote>(Arrays.asList(quotes));
	}

	public List<Quote> findQuoteEntries(int from, int to) {
		Quote[] quotes = restTemplate.getForObject(serviceURI
				+ "/findAllPaged?page=" + from + "&size=" + to, Quote[].class);
		return new ArrayList<Quote>(Arrays.asList(quotes));
	}

	public List<Quote> findQuoteEntries(PageRequest page) {
		Quote[] quotes = restTemplate.getForObject(
				serviceURI + "/findAllPaged?page=" + page.getPageNumber()
						+ "&size=" + page.getPageSize() + "&sort="
						+ page.getSort().getOrderFor("change1"), Quote[].class);
		return new ArrayList<Quote>(Arrays.asList(quotes));
	}

	public long countAllQuotes() {
		return restTemplate.getForObject(serviceURI + "/count", Integer.class);
	}

	public Map<String, Long> marketSummary() {
		return restTemplate.exchange(serviceURI + "/marketSummary",
				HttpMethod.GET, null,
				new ParameterizedTypeReference<Map<String, Long>>() {
				}).getBody();
	}

	private String setToQueryList(Set<String> set) {
		String[] s = set.toArray(new String[] {});
		String result = "";
		for (int i = 0; i < set.size(); i++) {
			result += s[i];
			if (i < set.size() - 1) {
				result += ",";
			}
		}
		return result;
	}

	public String getServiceURI() {
		return serviceURI;
	}

	public void setServiceURI(String s) {
		this.serviceURI = s;
	}

}
