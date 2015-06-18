package org.springframework.nanotrader.data.cloud;

import java.util.Map;

import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

public class QuoteWebServiceInfoCreator extends
		CloudFoundryServiceInfoCreator<WebServiceInfo> {

	public static final String QUOTES_PREFIX = "quoteService";

	public QuoteWebServiceInfoCreator() {
		super(new Tags(), QUOTES_PREFIX);
	}

	@Override
	public WebServiceInfo createServiceInfo(Map<String, Object> serviceData) {
		String id = (String) serviceData.get("name");

		Map<String, Object> credentials = getCredentials(serviceData);
		String uri = getUriFromCredentials(credentials);

		return new WebServiceInfo(id, uri);
	}
}
