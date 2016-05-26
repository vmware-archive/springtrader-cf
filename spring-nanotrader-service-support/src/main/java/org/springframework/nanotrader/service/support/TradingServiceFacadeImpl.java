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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.service.*;
import org.springframework.nanotrader.service.domain.CollectionResult;
import org.springframework.nanotrader.service.support.exception.AuthenticationException;
import org.springframework.nanotrader.service.support.exception.NoRecordsFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private QuoteService quoteService;

    @Autowired(required=false)
    private OrderGateway orderGateway;

    @Cacheable(value="authorizationCache")
    public Accountprofile findAccountprofileByAuthtoken(String token) {
        Accountprofile accountProfile = accountProfileService.findByAuthtoken(token);
        if (accountProfile != null) {
            return accountProfile;
        } else {
            log.error("TradingServiceFacadeImpl.findAccountprofileByAuthtoken(): accountProfile is null for token=" + token);
            throw new AuthenticationException("Authorization Token not found");
        }
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
        if(orderRequest != null && orderRequest.getQuote() != null) {
            orderRequest.setQuoteid(orderRequest.getQuote().getQuoteid());
        }
        tradingService.saveOrder(orderRequest);
        return orderRequest.getOrderid();
    }


    public Order findOrder(Long orderId, Long accountId) {
        if (log.isDebugEnabled()) {
            log.debug("TradingServiceFacade.findOrder: orderId=" + orderId + " accountId=" + accountId);
        }
        Order order =  orderService.find(orderId);
        if (order == null) {
            throw new NoRecordsFoundException();
        }

        order.setQuote(quoteService.findBySymbol(order.getQuoteid()));
        return order;
    }

    public CollectionResult findOrders(Long accountId, String status, Integer page, Integer pageSize) {
    	CollectionResult  collectionResults = new CollectionResult();
        if (log.isDebugEnabled()) {
            log.debug("OrderController.findOrders: accountId=" + accountId + " status" + status);
        }
        List<org.springframework.nanotrader.data.domain.Order> orders = null;

        collectionResults.setTotalRecords(tradingService.findCountOfOrders(accountId, status));
        collectionResults.setPage(page);
        collectionResults.setPageSize(pageSize);
        if (status != null) {
            orders = tradingService.findOrdersByStatus(accountId, status); //get by status
        } else {
            orders = tradingService.findOrders(accountId); //get all orders
        }

        List<Order> responseOrders = new ArrayList<Order>();
        if (orders != null && orders.size() > 0 ) {


            for(Order o: orders) {
                o.setQuote(quoteService.findBySymbol(o.getQuoteid()));
                responseOrders.add(o);
            }
        }
        collectionResults.setResults(responseOrders);
        return collectionResults;
    }
    
    public interface OrderGateway {
        void sendOrder(Order order);
    }
}