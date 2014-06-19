package com.gopivotal.cloudfoundry.example.appservices;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.UriBasedServiceInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	/**
	 * This simple controller just looks up the URI of the bound service and returns a JSP page that injects that URI.
	 * If there is more than one service bound, or the service bound does not have a URI then an error is thrown.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {

		// using spring cloud to get the single service bound to this app and get the uri
		// There must be a single service bound and it must contain the fields of a URIBasedServiceInfo
		// object, else an error will be displayed.
		CloudFactory cloudFactory = new CloudFactory();
		Cloud cloud = cloudFactory.getCloud();
		if (cloud.getServiceInfos().size() == 1 && cloud.getServiceInfos().get(0) instanceof UriBasedServiceInfo) {
			UriBasedServiceInfo serviceInfo = (UriBasedServiceInfo) cloud.getServiceInfos().get(0);
			// TODO: Currently this assumes no port, accurate if the http web service is deployed to CF, but may not be if the http web service is external
			// TODO: When UriBasedServiceInfo class exposes scheme, update to reflect whether it is http or https
			String uri = "http://" + serviceInfo.getHost() + "/" + serviceInfo.getPath();
			model.addAttribute("serviceURI", uri);
			return "home";
		} else {
			int numSvc = cloud.getServiceInfos().size();
			String serviceInfoName = ((numSvc == 1) ? cloud.getServiceInfos().get(0).getClass().getName() : "unknown");
			model.addAttribute("numServices", numSvc);
			model.addAttribute("type", serviceInfoName);
			return "error";
		}
	}


}
