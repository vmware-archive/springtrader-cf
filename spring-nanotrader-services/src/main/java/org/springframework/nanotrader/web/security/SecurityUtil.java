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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Provides utilities to work with SecurityContext
 * 
 * @author Brian Dussault
 */

@Service
public class SecurityUtil {

	private static final Logger LOG = LogManager.getLogger(SecurityUtil.class);

	public Long getAccountFromPrincipal() {
		return getPrincipal().getAccountId();
	}

	public Long getAccountFromPrincipal(Principal principal) {
		if(principal == null) {
			return null;
		}
		return ((CustomUser) principal).getAccountId();
	}
	
	public Long getAccountProfileIdFromPrincipal() {
		return getPrincipal().getAccountProfileId();
	}

	public Long getAccountProfileIdFromPrincipal(Principal principal) {
		if(principal == null) {
			return null;
		}

		return ((CustomUser) principal).getAccountProfileId();
	}

	public void checkAccountProfile(Long accountProfileId) {
		if (accountProfileId == null
				|| accountProfileId.compareTo(getAccountProfileIdFromPrincipal()) != 0) {
			throw new AccessDeniedException(null);
		}
	}

	public void checkAccount(Long accountId) {
		if (accountId == null
				|| accountId.compareTo(getAccountFromPrincipal()) != 0) {
			throw new AccessDeniedException(null);
		}
	}

	public void checkAccount(Principal principal, Long accountId) {
		if (accountId == null
				|| accountId.compareTo(getAccountFromPrincipal(principal)) != 0) {
			throw new AccessDeniedException(null);
		}
	}
	
	public String getUsernameFromPrincipal() { 
		return getPrincipal().getUsername();
	}
	
	private CustomUser getPrincipal() {
		if(SecurityContextHolder.getContext().getAuthentication() == null) {
			return null;
		}

		return (CustomUser) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
	}
}
