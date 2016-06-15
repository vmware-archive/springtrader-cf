package org.springframework.nanotrader.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.nanotrader.web.controller.AuthenticationController;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class CustomLogoutHandler extends SecurityContextLogoutHandler{

    private static final Logger LOG = LogManager.getLogger(CustomLogoutHandler.class);

    @Autowired
    private AccountProfileService accountProfileService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication auth) {

        Long id =  ((CustomUser) auth.getPrincipal()).getAccountProfileId();
		LOG.info("logging out: " + id);
//
//		if(principal != null) {
//			Long id =  ((CustomUser) principal).getAccountProfileId();
//			LOG.info("logout: accountProfileId=" + id);
			accountProfileService.logout(id);
//		}
    }
}