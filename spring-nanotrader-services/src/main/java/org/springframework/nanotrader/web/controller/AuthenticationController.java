/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.nanotrader.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.nanotrader.service.domain.AuthenticationRequest;
import org.springframework.nanotrader.service.support.exception.AuthenticationException;
import org.springframework.nanotrader.web.security.CustomAuthProvider;
import org.springframework.nanotrader.web.security.CustomUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Provides JSON based REST api to nanotrader login and logout services
 * 
 * @author Brian Dussault
 */

@RestController
public class AuthenticationController {

	private static final Logger LOG = LogManager.getLogger(AuthenticationController.class);

	@Autowired
	private AccountProfileService accountProfileService;

	@Autowired
	CustomAuthProvider authenticationProvider;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseStatus( HttpStatus.CREATED )
	@ResponseBody
	public Map<String, Object> login(HttpServletRequest request, @RequestBody AuthenticationRequest authenticationRequest) {
		return login(request, authenticationRequest.getUsername(), authenticationRequest.getPassword());
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseStatus( HttpStatus.OK )
	@ResponseBody
	public void logout() {
		LOG.info("logging out...");
////		Principal principal = request.getUserPrincipal();
////		LOG.info("logging out: " + principal);
////
////		if(principal != null) {
////			Long id =  ((CustomUser) principal).getAccountProfileId();
////			LOG.info("logout: accountProfileId=" + id);
////			accountProfileService.logout(id);
////		}
	}

	private Map<String, Object> login(HttpServletRequest request, String username, String password) {
		Accountprofile accountProfile  = accountProfileService.login(username, password);
		Map<String, Object> loginResponse;

		if (accountProfile != null) {
			loginResponse = new HashMap<String, Object>();
			List<Account> accounts = accountProfile.getAccounts();
			loginResponse.put("authToken", accountProfile.getAuthtoken());
			loginResponse.put("profileid", accountProfile.getProfileid());
			for (Account account: accounts) {
				loginResponse.put("accountid", account.getAccountid());
			}

			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
			Authentication authentication = this.authenticationProvider.authenticate(token);

			LOG.info("Logging in: " + authentication.getPrincipal());

			SecurityContext securityContext = SecurityContextHolder.getContext();
			securityContext.setAuthentication(authentication);

			HttpSession session = request.getSession(true);
			session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

		} else {
			LOG.error("login failed to find username=" + username + " password=" + password);
			throw new AuthenticationException("Login failed for user: " + username);
		}

		LOG.info("login success for " + username + " username::token=" + loginResponse.get("authToken"));

		return loginResponse;
	}
}
