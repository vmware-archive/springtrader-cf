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
package org.springframework.nanotrader.data.cloud;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Gary Russell
 * @author Brian Dussault
 */

@Repository
public interface OrderRepository {

//    @RequestLine("GET /orders/count")
//    Long count();

//    @RequestLine("DELETE /orders/")
//    @Headers("Content-Type: application/json")
//    void delete(@RequestBody Order order);

    @RequestLine("GET /orders/{id}")
    Order find(@Param(value = "id") Long id);

    @RequestLine("POST /orders/")
    @Headers("Content-Type: application/json")
    Order save(@RequestBody Order order);

//    @Modifying
//    @Transactional
//    @Query(value = "UPDATE Order o SET o.orderstatus = 'completed' WHERE o.accountid = ?1 AND o.orderstatus = 'closed'")
//    int updateClosedOrders(Long accountId);

//    @Query("SELECT o FROM Order o WHERE o.orderstatus = ?2 AND o.accountid  = ?1 order by orderid DESC")
//    List<Order> findOrdersByStatus(Long accountId, String status);

    // @Query("SELECT o FROM Order o WHERE o.accountid  = ?1 order by orderid DESC")
    @RequestLine("GET /orders?accountId={accountId}")
    List<Order> findOrdersByAccountid(@Param(value = "accountId") Long accountId);

    @RequestLine("GET /orders?accountId={accountId}&orderStatus={status}")
    List<Order> findOrdersByAccountidAndStatus(@Param(value = "accountId") Long accountId, @Param(value = "status") String status);

    @RequestLine("GET /orders/count?accountId={accountId}")
    Long findCountByAccountid(@Param(value = "accountId") Long accountId);

    @RequestLine("GET /orders/count?accountId={accountId}&orderStatus={status}")
    Long findCountByAccountidAndStatus(@Param(value = "accountId") Long accountId, @Param(value = "status") String status);

    @RequestLine("GET /orders/count")
    Long count();

    @RequestLine("GET /orders/")
    List<Order> findAll();

//    @Query("SELECT o FROM Order o WHERE o.orderid = ?1 AND o.accountid  = ?2")
//    @RequestLine("GET /orders/{orderId}/account/{accountId}")
//    List<Order>  findByOrderidAndAccountAccountid(@Param(value = "orderId") Long orderId, @Param(value = "accountId") Long accountId);

//    @Query("SELECT count(o) FROM Order o WHERE o.accountid  = ?1")
//    @RequestLine("GET /orders/account/{accountId}/count")
//    Long findCountOfOrders(@Param(value = "accountId") Long accountId);

//    @Query("SELECT count(o) FROM Order o WHERE o.accountid  = ?1 and o.orderstatus = ?2")
//    Long findCountOfOrders(Long accountId, String status);

}
