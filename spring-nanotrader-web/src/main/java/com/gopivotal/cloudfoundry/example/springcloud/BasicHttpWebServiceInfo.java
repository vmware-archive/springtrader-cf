package com.gopivotal.cloudfoundry.example.springcloud;

import org.springframework.cloud.CloudException;
import org.springframework.cloud.service.UriBasedServiceInfo;
import org.springframework.cloud.util.UriInfo;
import org.springframework.cloud.service.ServiceInfo.ServiceLabel;

public class BasicHttpWebServiceInfo extends UriBasedServiceInfo {

	public BasicHttpWebServiceInfo(String id, String host, int port, String username, String password, String virtualHost) {
		super(id, "http", host, port, username, password, virtualHost);
	}

	public BasicHttpWebServiceInfo(String id, String uri)	throws CloudException {
		super(id, uri);
	}

	@Override
	protected UriInfo validateAndCleanUriInfo(UriInfo uriInfo) {
		if (!"http".equals(uriInfo.getScheme())) {
			throw new IllegalArgumentException("wrong scheme in httpwebservice URI: " + uriInfo);
		}

		return new UriInfo(uriInfo.getScheme(), uriInfo.getHost(), uriInfo.getPort(), uriInfo.getUserName(), uriInfo.getPassword(), uriInfo.getPath());
	}
}
