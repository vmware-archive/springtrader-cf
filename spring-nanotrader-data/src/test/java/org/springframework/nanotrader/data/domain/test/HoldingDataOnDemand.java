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
package org.springframework.nanotrader.data.domain.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.nanotrader.data.service.HoldingService;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;


@Component
@Configurable
public class HoldingDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<Holding> data;

	@Autowired
    HoldingService holdingService;

	public Holding getNewTransientHolding(int index) {
        Holding obj = new Holding();
        setAccountAccountid(obj, index);
        setPurchasedate(obj, index);
        setPurchaseprice(obj, index);
        setQuantity(obj, index);
        setQuoteSymbol(obj, index);
        return obj;
    }

	public void setAccountAccountid(Holding obj, int index) {
        Long accountAccountid = new Long(index);
        obj.setAccountAccountid(accountAccountid);
    }

	public void setPurchasedate(Holding obj, int index) {
        Date purchasedate = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setPurchasedate(purchasedate);
    }

	public void setPurchaseprice(Holding obj, int index) {
        BigDecimal purchaseprice = BigDecimal.valueOf(index, 2);
       
        if (purchaseprice.compareTo(new BigDecimal("999999999999.99")) == 1) {
            purchaseprice = new BigDecimal("999999999999.99");
        }
        obj.setPurchaseprice(purchaseprice);
    }

	public void setQuantity(Holding obj, int index) {
		BigDecimal quantity = BigDecimal.valueOf(index);
        obj.setQuantity(quantity);
    }

	public void setQuoteSymbol(Holding obj, int index) {
        String quoteSymbol = "quoteSymbol_" + index;
        if (quoteSymbol.length() > 250) {
            quoteSymbol = quoteSymbol.substring(0, 250);
        }
        obj.setQuoteSymbol(quoteSymbol);
    }

	public Holding getRandomHolding() {
        init();
        Holding obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getHoldingid();
        return holdingService.find(id);
    }

	public boolean modifyHolding(Holding obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = holdingService.findAll();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Holding' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }

        data = new ArrayList<Holding>();
        for (int i = 0; i < 10; i++) {
            Holding obj = getNewTransientHolding(i);
            try {
                holdingService.save(obj);
            } catch (ConstraintViolationException e) {
                StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getConstraintDescriptor()).append(":").append(cv.getMessage()).append("=").append(cv.getInvalidValue()).append("]");
                }
                throw new RuntimeException(msg.toString(), e);
            }
            data.add(obj);
        }
    }
}
