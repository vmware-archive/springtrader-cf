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

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.nanotrader.data.domain.Quote;
import org.springframework.nanotrader.data.service.*;
import org.springframework.nanotrader.service.domain.*;
import org.springframework.nanotrader.service.support.exception.AuthenticationException;
import org.springframework.nanotrader.service.support.exception.NoRecordsFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
* Facade that, generally, delegates directly to a {@link TradingService},
* after mapping from service domain to data domain. For {@link #saveOrder(Order, boolean)},
* and option for synch/asynch processing is provided.
* @author Gary Russell
* @author Brian Dussault
* @author Kashyap Parikh
*/
@Service
public class TradingServiceFacadeImpl implements TradingServiceFacade {

    private static Logger log = LoggerFactory.getLogger(TradingServiceFacadeImpl.class);

    private String ORDER_MAPPING = "order";

    private static String HOLDING_MAPPING = "holding";

    private static String QUOTE_MAPPING = "quote";

    private static final String ACCOUNT_PROFILE_MAPPING = "accountProfile";

    private static final String ACCOUNT_MAPPING = "account";

    private static final String PORTFOLIO_SUMMARY_MAPPING = "portfolioSummary";

    private static final String MARKET_SUMMARY_MAPPING = "marketSummary";

    private static final String HOLDING_SUMMARY_MAPPING = "holdingSummary";

    @Autowired
    private TradingService tradingService;

    @Autowired
    private AccountProfileService accountProfileService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private HoldingService holdingService;

    @Autowired
    private OrderService orderService;

    @Autowired
    @Qualifier( "rtQuoteService")
    QuoteService quoteService;

    @Resource
    private Mapper mapper;

    @Autowired(required=false)
    private OrderGateway orderGateway;

    @Cacheable(value="authorizationCache")
    public Accountprofile findAccountprofileByAuthtoken(String token) {
        Accountprofile accountProfileResponse;
        org.springframework.nanotrader.data.domain.Accountprofile accountProfile = accountProfileService.findByAuthtoken(token);
        if (accountProfile != null) {
            accountProfileResponse = new Accountprofile();
            mapper.map(accountProfile, accountProfileResponse, ACCOUNT_PROFILE_MAPPING);
        } else {
            log.error("TradingServiceFacadeImpl.findAccountprofileByAuthtoken(): accountProfile is null for token=" + token);
            throw new AuthenticationException("Authorization Token not found");
        }

        return accountProfileResponse;
    }

    public Map<String, Object> login(String username, String password) {

        org.springframework.nanotrader.data.domain.Accountprofile accountProfile  = accountProfileService.login(username, password);
        Map<String, Object> loginResponse = null;

        if (accountProfile != null) {
            loginResponse = new HashMap<String, Object>();
            List<org.springframework.nanotrader.data.domain.Account> accounts = accountProfile.getAccounts();
            loginResponse.put("authToken", accountProfile.getAuthtoken());
            loginResponse.put("profileid", accountProfile.getProfileid());
            for (org.springframework.nanotrader.data.domain.Account account: accounts) {
                loginResponse.put("accountid", account.getAccountid());
            }
        } else {
            log.error("TradingServiceFacade.login failed to find username=" + username + " password" + password);
            throw new AuthenticationException("Login failed for user: " + username);
        }

        if (log.isDebugEnabled()) {
            log.error("TradingServiceFacade.login success for " + username + " username::token=" + loginResponse.get("authToken"));
        }
        return loginResponse;

    }

    @CacheEvict(value="authorizationCache")
    public void logout(String authtoken) {

        if (log.isDebugEnabled()) {
            log.error("TradingServiceFacade.logout: username::token=" + authtoken);
        }
        accountProfileService.logout(authtoken);
    }

    public Accountprofile findAccountProfile(Long id) {
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.findAccountProfile: id=" + id);
        }
        org.springframework.nanotrader.data.domain.Accountprofile accountProfile = accountProfileService.findAccountProfile(id);
        Accountprofile accountProfileResponse = new Accountprofile();
        if (accountProfile == null) {
            throw new NoRecordsFoundException();
        }

        mapper.map(accountProfile, accountProfileResponse, "accountProfile");
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.find - after service call. Payload is: "
                    + accountProfileResponse);
        }

        return accountProfileResponse;
    }

    public Long saveAccountProfile(Accountprofile accountProfileRequest) {
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.saveAccountProfile:"
                    + accountProfileRequest.toString());
        }

        org.springframework.nanotrader.data.domain.Accountprofile accountProfile = new org.springframework.nanotrader.data.domain.Accountprofile();
        mapper.map(accountProfileRequest, accountProfile);

        //initialize the new account
        org.springframework.nanotrader.data.domain.Account account = accountProfile.getAccounts().iterator().next();
        account.setLogincount(0);
        account.setLogoutcount(0);
        account.setBalance(account.getOpenbalance());
        account.setCreationdate(new Date());

        org.springframework.nanotrader.data.domain.Accountprofile createdAccountProfile = accountProfileService.saveAccountProfile(accountProfile);

        return createdAccountProfile.getProfileid();
    }


    public void updateAccountProfile(Accountprofile accountProfileRequest, String username) {
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.updateAccountProfile:"
                    + accountProfileRequest.toString());
        }
        accountProfileRequest.setAccounts(null); //dont expect this to be populated by the client
        org.springframework.nanotrader.data.domain.Accountprofile accountProfile = new org.springframework.nanotrader.data.domain.Accountprofile();
        mapper.map(accountProfileRequest, accountProfile);
        accountProfileService.updateAccountProfile(accountProfile, username);
    }

    public Holding findHolding(Long id, Long accountId) {
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.findHolding: id=" + id);
        }
        Holding holdingResponse = new Holding();
        org.springframework.nanotrader.data.domain.Holding holding = holdingService.find(id);
        if (holding == null) {
            throw new NoRecordsFoundException();
        }
        Set<String> symbol = new HashSet<String>();
        symbol.add(holding.getQuoteSymbol());
        Map<String, Quote> currentQuote = getCurrentQuotes(symbol);
        mapper.map(holding, holdingResponse);
        holdingResponse.setQuote(currentQuote.get(holding.getQuoteSymbol()));
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.findHolding - after service call. Payload is: " + holdingResponse);
        }
        return holdingResponse;
    }


    public CollectionResult findHoldingsByAccountId(Long accountId) {
        CollectionResult  collectionResults = new CollectionResult();


        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.findHoldingsByAccount: id=" + accountId);
        }


        List<Holding> holdingResponse = new ArrayList<Holding>();
        List<org.springframework.nanotrader.data.domain.Holding> holdings = holdingService.findByAccountid(accountId);

        if (holdings != null  &&  holdings.size() > 0) {
            Set<String> symbols = new HashSet<String>();
            for (org.springframework.nanotrader.data.domain.Holding h: holdings) {
                //get unique quotes symbols
                symbols.add(h.getQuoteSymbol());
            }

            Map<String, Quote> currentQuotes = getCurrentQuotes(symbols);
            for(org.springframework.nanotrader.data.domain.Holding h: holdings) {
                Holding holding = new Holding();
                mapper.map(h, holding, HOLDING_MAPPING);
                holding.setQuote(currentQuotes.get(h.getQuoteSymbol()));
                holdingResponse.add(holding);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.findHoldingsByAccountId completed");
        }
        collectionResults.setResults(holdingResponse);
        return collectionResults;
    }


    public Long saveOrder(Order orderRequest, boolean synch) {
        if (synch) {

        	return saveOrderDirect(orderRequest);
        }
        else {
            orderGateway.sendOrder(orderRequest);

            return null;
        }

    }

    public Long saveOrderDirect(Order orderRequest) {
        org.springframework.nanotrader.data.domain.Order order = new org.springframework.nanotrader.data.domain.Order();
        mapper.map(orderRequest, order, ORDER_MAPPING);
        if(orderRequest != null && orderRequest.getQuote() != null) {
            order.setQuoteid(orderRequest.getQuote().getQuoteid());
        }
        tradingService.saveOrder(order);
        return order.getOrderid();
    }


    public Order findOrder(Long orderId, Long accountId) {
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.findOrder: orderId=" + orderId + " accountId=" + accountId);
        }
        org.springframework.nanotrader.data.domain.Order order =  orderService.find(orderId);
        if (order == null) {
            throw new NoRecordsFoundException();
        }
        Order responseOrder = new Order();
        mapper.map(order, responseOrder, ORDER_MAPPING);

        Quote q = new Quote();
        mapper.map(quoteService.findBySymbol(order.getQuoteid()), q);

        responseOrder.setQuote(q);
        return responseOrder;
    }

    public CollectionResult findOrders(Long accountId, String status, Integer page, Integer pageSize) {
    	CollectionResult  collectionResults = new CollectionResult();
        if (log.isDebugEnabled()) {
            log.debug("OrderController.findOrders: accountId=" + accountId + " status" + status);
        }
        List<org.springframework.nanotrader.data.domain.Order> orders = null;

        collectionResults.setTotalRecords(tradingService.findCountOfOrders(accountId, status));
        if (status != null) {
            orders = tradingService.findOrdersByStatus(accountId, status); //get by status
        } else {
            orders = tradingService.findOrders(accountId); //get all orders
        }


        List<Order> responseOrders = new ArrayList<Order>();
        if (orders != null && orders.size() > 0 ) {


            for(org.springframework.nanotrader.data.domain.Order o: orders) {
                Order order = new Order();
                mapper.map(o, order, ORDER_MAPPING);

                Quote q = new Quote();
                mapper.map(quoteService.findBySymbol(o.getQuoteid()), q);

                order.setQuote(q);
                responseOrders.add(order);
            }
        }
        collectionResults.setResults(responseOrders);

        return collectionResults;
    }

    public CollectionResult findQuotes() {
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade: findQuotes");
        }
        CollectionResult  collectionResults = new CollectionResult();
        List<org.springframework.nanotrader.data.domain.Quote> quotes = quoteService.findAllQuotes(); //get all quotes;
        long totalRecords = quotes.size();
        collectionResults.setTotalRecords(totalRecords);
        List<Quote> responseQuotes = new ArrayList<Quote>();
        if (quotes.size() > 0 ) {
            for(org.springframework.nanotrader.data.domain.Quote o: quotes) {
                Quote quote = new Quote();
                mapper.map(o, quote, QUOTE_MAPPING);
                responseQuotes.add(quote);
            }
        }
        collectionResults.setResults(responseQuotes);
        return collectionResults;
    }

    public Account findAccount(Long id) {
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.findAccount: id=" + id);
        }

        Account accountResponse = new Account();
        org.springframework.nanotrader.data.domain.Account account = accountService.findAccount(id);
        if (account == null) {
            throw new NoRecordsFoundException();
        }
        
        mapper.map(account, accountResponse, ACCOUNT_MAPPING);
        
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.findAccount - after service call. Payload is: " + accountResponse);
        }
        return accountResponse;
    }
    

    

    private Map<String, Quote> getCurrentQuotes(Set<String> symbols) { 
        List<org.springframework.nanotrader.data.domain.Quote> quotes = quoteService.findBySymbolIn(symbols);
        Map<String, Quote> currentQuotes = new HashMap<String, Quote>();
        for (org.springframework.nanotrader.data.domain.Quote q: quotes) {
            Quote quote = new Quote();
            mapper.map(q, quote);
            currentQuotes.put(q.getSymbol(), quote);
        }
        return currentQuotes;
    }
    
    public Quote findQuoteBySymbol(String symbol) {
        Quote quote = quoteService.findBySymbol(symbol);
        if (quote == null) {
            throw new NoRecordsFoundException();
        }
        return quote;
    }
    
    public static interface OrderGateway {
        void sendOrder(Order order);
    }
}