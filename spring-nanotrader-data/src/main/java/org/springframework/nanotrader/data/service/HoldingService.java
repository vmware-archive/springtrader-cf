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
package org.springframework.nanotrader.data.service;

import org.springframework.nanotrader.data.domain.Order;

import java.util.List;

public interface HoldingService {

    long countAllOrders();

    void deleteOrder(Order order);

    Order findOrder(Long id);

    List<Order> findAllOrders();

    List<Order> findOrderEntries(int firstResult, int maxResults);

    Order saveOrder(Order order);

    Order findByOrderIdAndAccountId(Long orderId, Long accountId);

    long countOfOrders(Long accountId, String status);

    long countOfOrders(Long accountId);

    List<Order> findOrdersByStatus(Long accountId, String status);

    List<Order> findOrdersByAccountid(Long accountId);

    int updateClosedOrders(Long accountId);
}
