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
package org.springframework.nanotrader.data.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.nanotrader.data.domain.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Gary Russell
 * @author Brian Dussault
 */

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE Order o SET o.orderstatus = 'completed' WHERE o.accountid = ?1 AND o.orderstatus = 'closed'")
    int updateClosedOrders(Long accountId);

    @Query("SELECT o FROM Order o WHERE o.orderstatus = ?2 AND o.accountid  = ?1 order by orderid DESC")
    List<Order> findOrdersByStatus(Long accountId, String status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.accountid  = ?1 order by orderid DESC")
    List<Order> findOrdersByAccountAccountid_Accountid(Long accountId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderid = ?1 AND o.accountid  = ?2")
    Order findByOrderidAndAccountAccountid(Integer orderId, Long accountId);

    @Query("SELECT count(o) FROM Order o WHERE o.accountid  = ?1")
    Long findCountOfOrders(Long accountId);

    @Query("SELECT count(o) FROM Order o WHERE o.accountid  = ?1 and o.orderstatus = ?2")
    Long findCountOfOrders(Long accountId, String status);

}
