package org.springframework.nanotrader.data.cloud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

public class QuoteWebServiceInfoCreator extends CloudFoundryServiceInfoCreator<WebServiceInfo> {

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
    
    @SuppressWarnings("unchecked")
	protected Map<String, Object> getCredentials(Map<String, Object> serviceData) {
		return (Map<String, Object>) serviceData.get("credentials");
	}
    
    protected String getUriFromCredentials(Map<String, Object> credentials) {
		List<String> keys = new ArrayList<String>();
		//keys.addAll(Arrays.asList("uri", "url"));

		//for (String uriScheme : uriSchemes) {
			keys.add(getUriScheme() + "Uri");
			keys.add(getUriScheme() + "uri");
			keys.add(getUriScheme() + "Url");
			keys.add(getUriScheme() + "url");
		//}

		return getStringFromCredentials(credentials, keys.toArray(new String[keys.size()]));
	}
}
