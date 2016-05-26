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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.nanotrader.service.domain.AuthenticationRequest;
import org.springframework.nanotrader.service.support.exception.AuthenticationException;
import org.springframework.nanotrader.web.security.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Provides JSON based REST api to nanotrader login and logout services
 * 
 * @author Brian Dussault
 */

@Controller
public class AuthenticationController {

	private static final Logger LOG = Logger.getLogger(AuthenticationController.class);

	@Autowired
	private AccountProfileService accountProfileService;

	@Autowired
	private SecurityUtil securityUtil;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseStatus( HttpStatus.CREATED )
	@ResponseBody
	public Map<String, Object> login(@RequestBody AuthenticationRequest authenticationRequest) {
		return login(authenticationRequest.getUsername(), authenticationRequest.getPassword());
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseStatus( HttpStatus.OK )
	@ResponseBody
	public void logout() {
		logout(securityUtil.getAuthToken());
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseStatus( HttpStatus.METHOD_NOT_ALLOWED )
	public void get() {
		
	}

	private Map<String, Object> login(String username, String password) {
		Accountprofile accountProfile  = accountProfileService.login(username, password);
		Map<String, Object> loginResponse;

		if (accountProfile != null) {
			loginResponse = new HashMap<String, Object>();
			List<Account> accounts = accountProfile.getAccounts();
			loginResponse.put("authToken", accountProfile.getAuthtoken());
			loginResponse.put("profileid", accountProfile.getProfileid());
			for (org.springframework.nanotrader.data.domain.Account account: accounts) {
				loginResponse.put("accountid", account.getAccountid());
			}
		} else {
			LOG.error("login failed to find username=" + username + " password" + password);
			throw new AuthenticationException("Login failed for user: " + username);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("login success for " + username + " username::token=" + loginResponse.get("authToken"));
		}
		return loginResponse;
	}

	@CacheEvict(value="authorizationCache")
	private void logout(String authtoken) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("logout: username::token=" + authtoken);
		}
		accountProfileService.logout(authtoken);
	}
}
