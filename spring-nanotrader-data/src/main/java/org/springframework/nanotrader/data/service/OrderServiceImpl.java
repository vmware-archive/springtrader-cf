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

import com.netflix.discovery.DiscoveryClient;
import feign.Feign;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.nanotrader.data.cloud.OrderDecoder;
import org.springframework.nanotrader.data.cloud.OrderEncoder;
import org.springframework.nanotrader.data.cloud.OrderRepository;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Profile({"default", "cloud"})
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LogManager.getLogger(OrderServiceImpl.class);

    @Autowired
    DiscoveryClient discoveryClient;

    private OrderRepository orderRepository;

    @Autowired
    String orderRepositoryName;

	public Long countAllOrders() {
        return orderRepository().count();
    }

    public Order find(Long id) {
        return orderRepository().find(id);
    }

	public List<Order> findAllOrders() {
        return orderRepository().findAll();
    }

    public Order saveOrder(Order order) {
        return orderRepository().save(order);
    }

    public List<Order> findByAccountId(Long accountId) {
        return orderRepository().findOrdersByAccountid(accountId);
    }

    public Long countOfOrders(Long accountId, String status) {
        return orderRepository().findCountByAccountidAndStatus(accountId, status);
    }

    public Long countOfOrders(Long accountId) {
        return orderRepository().findCountByAccountid(accountId);
    }

    public List<Order> findOrdersByStatus(Long accountId, String status) {
        return orderRepository().findOrdersByAccountidAndStatus(accountId, status);
    }

    private OrderRepository orderRepository() {
        if (this.orderRepository == null) {
            LOG.info("initializing orderRepository named: " + orderRepositoryName);
            String url = discoveryClient.getNextServerFromEureka(
                    orderRepositoryName, false).getHomePageUrl();

            LOG.info("orderRepository url is: " + url);

            this.orderRepository = Feign.builder()
                    .encoder(new OrderEncoder())
                    .decoder(new OrderDecoder())
                    .target(OrderRepository.class, url);

            LOG.info("orderRepository initialization complete.");
        }
        return this.orderRepository;
    }
}
