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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.nanotrader.data.service.TradingService;
import org.springframework.stereotype.Service;

/**
 * Facade that, generally, delegates directly to a {@link TradingService},
 * after mapping from service domain to data domain. For {@link #saveOrder(Order, boolean)},
 * and option for synch/asynch processing is provided.
 *
 * @author Gary Russell
 * @author Brian Dussault
 * @author Kashyap Parikh
 */
@Service
public class TradingServiceFacadeImpl implements TradingServiceFacade {

    @Autowired
    private TradingService tradingService;

    @Autowired(required = false)
    private OrderGateway orderGateway;

    public Long saveOrder(Order orderRequest, boolean sync) {
        if (sync) {
            return saveOrderDirect(orderRequest);
        } else {
            orderGateway.sendOrder(orderRequest);
            return null;
        }
    }

    public Long saveOrderDirect(Order orderRequest) {
        if (orderRequest != null && orderRequest.getQuote() != null) {
            orderRequest.setQuoteid(orderRequest.getQuote().getQuoteid());
        }
        tradingService.saveOrder(orderRequest);
        return orderRequest.getOrderid();
    }

    public interface OrderGateway {
        void sendOrder(Order order);
    }
}