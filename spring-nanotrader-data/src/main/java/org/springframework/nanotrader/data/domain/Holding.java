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
package org.springframework.nanotrader.data.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Holding implements Serializable {

    private Long holdingid;

    public Long getHoldingid() {
        return this.holdingid;
    }

    public void setHoldingid(Long id) {
        this.holdingid = id;
    }

    private List<Order> orders;

    private BigDecimal purchaseprice;

    private BigDecimal quantity;

    private Date purchasedate;

    private Long accountAccountid;

    private String quoteSymbol;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public BigDecimal getPurchaseprice() {
        return purchaseprice;
    }

    public void setPurchaseprice(BigDecimal purchaseprice) {
        this.purchaseprice = purchaseprice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Date getPurchasedate() {
        return purchasedate;
    }

    public void setPurchasedate(Date purchasedate) {
        this.purchasedate = purchasedate;
    }

    public Long getAccountAccountid() {
        return accountAccountid;
    }

    public void setAccountAccountid(Long accountAccountid) {
        this.accountAccountid = accountAccountid;
    }

    public String getQuoteSymbol() {
        return quoteSymbol;
    }

    public void setQuoteSymbol(String quoteSymbol) {
        this.quoteSymbol = quoteSymbol;
    }

    @Override
    public String toString() {
        return "Holding [holdingid=" + holdingid + ", purchaseprice=" + purchaseprice + ", quantity=" + quantity
                + ", purchasedate=" + purchasedate + ", quoteSymbol=" + quoteSymbol + "]";
    }


}
