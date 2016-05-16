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
package org.springframework.nanotrader.service.support;

import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.nanotrader.data.service.AccountService;
import org.springframework.nanotrader.data.service.QuoteService;
import org.springframework.nanotrader.data.service.TradingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;



/**
 * @author Ilayaperumal Gopinathan
 * 
 */
@Service
public class AdminServiceFacadeImpl implements AdminServiceFacade {

	private static Logger log = Logger.getLogger(AdminServiceFacadeImpl.class);

	@Resource
	@Qualifier( "rtQuoteService")
	private QuoteService quoteService;

	@Resource
	private AccountProfileService accountProfileService;

	@Resource
	private AccountService accountService;

	@Resource
	private TradingService tradingService;

	@Resource
	private TradingServiceFacade tradingServiceFacade;

	@Resource
	private Mapper mapper;
}
