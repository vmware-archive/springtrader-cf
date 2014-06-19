package com.gopivotal.cloudfoundry.example.springcloud;

import java.util.Map;

import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

public class BasicHttpWebServiceInfoCreator extends CloudFoundryServiceInfoCreator<BasicHttpWebServiceInfo>{

	public BasicHttpWebServiceInfoCreator() {
		super(new Tags(), "http");
	}
	
	public BasicHttpWebServiceInfo createServiceInfo(Map<String,Object> serviceData) {
		@SuppressWarnings("unchecked")
		Map<String,Object> credentials = (Map<String, Object>) serviceData.get("credentials");

		String id = (String) serviceData.get("name");

		String uri = getStringFromCredentials(credentials, "uri", "url");
		
		return new BasicHttpWebServiceInfo(id, uri);
	}
}
