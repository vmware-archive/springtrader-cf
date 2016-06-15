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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.nanotrader.web.security.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

/**
 * Provides JSON based REST api to Accountprofile repository
 * 
 * @author Brian Dussault
 */

@Controller
public class AccountProfileController {

	@Autowired
	private AccountProfileService accountProfileService;

	@Autowired
	private SecurityUtil securityUtil;

	@RequestMapping(value = "/accountProfile/{id}", method = RequestMethod.GET)
	public  ResponseEntity<Accountprofile> find(@PathVariable("id") final Long id) {
//		securityUtil.checkAccountProfile(id);
		Accountprofile accountProfile = accountProfileService.findAccountProfile(id);
		return new ResponseEntity<Accountprofile>(accountProfile, BaseController.getNoCacheHeaders(),
				HttpStatus.OK);
		
	}

	@RequestMapping(value = "/accountProfile", method = RequestMethod.POST)
	public ResponseEntity<String> save(@RequestBody Accountprofile accountProfileRequest,  UriComponentsBuilder builder) {
		//initialize the new account
		org.springframework.nanotrader.data.domain.Account account = accountProfileRequest.getAccounts().iterator().next();
		account.setLogincount(0);
		account.setLogoutcount(0);
		account.setBalance(account.getOpenbalance());
		account.setCreationdate(new Date());

		Long accountProfileId = accountProfileService.saveAccountProfile(accountProfileRequest).getProfileid();

		HttpHeaders responseHeaders = new HttpHeaders();   
		responseHeaders.setLocation(builder.path("/accountProfile/{id}").buildAndExpand(accountProfileId).toUri());
		return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/accountProfile/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void update(@PathVariable("id") final Long id, @RequestBody Accountprofile accountProfileRequest) {
		securityUtil.checkAccountProfile(id);
		accountProfileRequest.setProfileid(id);
		accountProfileService.updateAccountProfile(accountProfileRequest, securityUtil.getUsernameFromPrincipal());
	}

	@RequestMapping(value = "/accountProfile/{id}", method = RequestMethod.DELETE)
	@ResponseStatus( HttpStatus.METHOD_NOT_ALLOWED )
	public void delete() {
		
	}
}
