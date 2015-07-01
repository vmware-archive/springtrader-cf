package org.springframework.nanotrader.data.cloud;

import java.util.Map;

import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

public class MarketServiceWebServiceInfoCreator extends
		CloudFoundryServiceInfoCreator<WebServiceInfo> {

	public static final String MARKET_PREFIX = "marketService";

	public MarketServiceWebServiceInfoCreator() {
		super(new Tags(), MARKET_PREFIX);
	}

	@Override
	public WebServiceInfo createServiceInfo(Map<String, Object> serviceData) {
		String id = (String) serviceData.get("name");

		Map<String, Object> credentials = getCredentials(serviceData);
		String uri = getUriFromCredentials(credentials);

		return new WebServiceInfo(id, uri);
	}
}
