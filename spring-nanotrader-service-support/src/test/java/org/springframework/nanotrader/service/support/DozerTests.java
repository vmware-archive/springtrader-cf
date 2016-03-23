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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class DozerTests {

    @Autowired
    private Mapper mapper;

    @Test
    public void testProfileMapping() {
        org.springframework.nanotrader.data.domain.Accountprofile apd = new org.springframework.nanotrader.data.domain.Accountprofile();
        apd.setProfileid(123L);
        apd.setAddress("address");
        apd.setAuthtoken("auth");
        apd.setCreditcard("cc");
        apd.setEmail("email");
        apd.setFullname("name");
        apd.setPasswd("pass");
        apd.setUserid("uid");

        org.springframework.nanotrader.data.domain.Account ad = new org.springframework.nanotrader.data.domain.Account();
        ad.setAccountid(245L);
        ad.setAccountprofile(apd);
        ad.setCreationdate(new Date());
        ad.setBalance(BigDecimal.ONE);
        ad.setLastlogin(new Date());
        ad.setLogincount(2);
        ad.setLogoutcount(3);
        ad.setOpenbalance(BigDecimal.TEN);
        ad.setVersion(1);

        apd.addAccount(ad);

        org.springframework.nanotrader.service.domain.Accountprofile aps = new org.springframework.nanotrader.service.domain.Accountprofile();
        mapper.map(apd, aps);

        assertNotNull(aps);
        assertEquals("123", aps.getProfileid().toString());
        assertEquals("address", aps.getAddress());
        assertEquals("cc", aps.getCreditcard());
        assertEquals("email", aps.getEmail());
        assertEquals("name", aps.getFullname());
        assertEquals("pass", aps.getPasswd());
        assertEquals("uid", aps.getUserid());

        assertNotNull(aps.getAccounts());
        assertTrue(aps.getAccounts().size() == 1);

        Map as = aps.getAccounts().get(0);
        assertNotNull(as);
        assertEquals("245", as.get("accountid").toString());
        assertEquals(BigDecimal.ONE, as.get("balance"));
        assertNotNull(as.get("creationdate"));
        assertNotNull(as.get("lastlogin"));
        assertEquals(2, as.get("logincount"));
        assertEquals(3, as.get("logoutcount"));
        assertEquals(BigDecimal.TEN, as.get("openbalance"));

        org.springframework.nanotrader.data.domain.Accountprofile apd2 = new org.springframework.nanotrader.data.domain.Accountprofile();

        mapper.map(aps, apd2);
        assertNotNull(apd2);
        assertEquals("123", apd2.getProfileid().toString());
        assertEquals("address", apd2.getAddress());
        assertEquals("cc", apd2.getCreditcard());
        assertEquals("email", apd2.getEmail());
        assertEquals("name", apd2.getFullname());
        assertEquals("pass", apd2.getPasswd());
        assertEquals("uid", apd2.getUserid());

        assertNotNull(apd2.getAccounts());
        assertTrue(aps.getAccounts().size() == 1);

        org.springframework.nanotrader.data.domain.Account as2 = apd2.getAccounts().get(0);
        assertNotNull(as2);
        assertEquals("245", as2.getAccountid().toString());
        assertEquals(BigDecimal.ONE, as2.getBalance());
        assertNotNull(as2.getCreationdate());
        assertNotNull(as2.getLastlogin());
        assertEquals("2", as2.getLogincount().toString());
        assertEquals("3", as2.getLogoutcount().toString());
        assertEquals(BigDecimal.TEN, as2.getOpenbalance());
    }
}
