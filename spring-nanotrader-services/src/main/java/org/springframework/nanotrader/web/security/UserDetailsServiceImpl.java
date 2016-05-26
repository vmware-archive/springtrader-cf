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
package org.springframework.nanotrader.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.nanotrader.service.support.exception.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *  UserDetailsServiceImpl provides authentication lookup service which validates the http header token
 *  
 *  @author Brian Dussault 
 */

@Service 
public class UserDetailsServiceImpl implements UserDetailsService {

	private static Logger LOG = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	private AccountProfileService accountProfileService;
	
	@Override
	public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException  {
		if (token == null) {
			LOG.error("UserDetailsServiceImpl.loadUserByUsername(): User not found with null token");
			throw new UsernameNotFoundException("UserDetailsServiceImpl.loadUserByUsername(): User not found with null token");
		}
		Accountprofile accountProfile;
		try { 
			accountProfile = findAccountprofileByAuthtoken(token);
		} catch (AuthenticationException ae) { 
			throw new UsernameNotFoundException("UserDetailsServiceImpl.loadUserByUsername(): User not found with token:" + token);
		}

		List<Account> accounts = accountProfile.getAccounts();
		Long accountId = null;
		for(Account account: accounts ) {
			accountId = account.getAccountid();
		}
	
		User user = new CustomUser(accountProfile.getUserid(), accountProfile.getPasswd(), getAuthorities(accountProfile.getUserid()), accountId, accountProfile.getProfileid(), token);
		if (LOG.isDebugEnabled()) {
			LOG.debug("UserDetailsServiceImpl.loadUserByUsername(): user=" + user  + " username::token" + token);
		}
		
		return user;
	}

	private List<GrantedAuthority> getAuthorities(String userId) {
		List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>(1);
		if ("admin".equals(userId)) { 
			authList.add(new SimpleGrantedAuthority("ROLE_API_ADMIN"));
		} else { 
			authList.add(new SimpleGrantedAuthority("ROLE_API_USER"));
		}
        return authList;
    }

	@Cacheable(value="authorizationCache")
	private Accountprofile findAccountprofileByAuthtoken(String token) {
		Accountprofile accountProfile = accountProfileService.findByAuthtoken(token);
		if (accountProfile != null) {
			return accountProfile;
		} else {
			LOG.error("findAccountprofileByAuthtoken(): accountProfile is null for token=" + token);
			throw new AuthenticationException("Authorization Token not found");
		}
	}
}
