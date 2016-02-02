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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Accountprofile accountprofile;

    private Date creationdate;

    private BigDecimal openbalance;

    @NotNull
    private Integer logoutcount = 0;

    private BigDecimal balance;

    private Date lastlogin;

    @NotNull
    private Integer logincount = 0;

    private int version = 0;

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public Accountprofile getAccountprofile() {
        return accountprofile;
    }

    public void setAccountprofile(Accountprofile a) {
        this.accountprofile = a;
    }

    public Date getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    public BigDecimal getOpenbalance() {
        return openbalance;
    }

    public void setOpenbalance(BigDecimal openbalance) {
        this.openbalance = openbalance;
    }

    public Integer getLogoutcount() {
        return logoutcount;
    }

    public void setLogoutcount(Integer logoutcount) {
        this.logoutcount = logoutcount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(Date lastlogin) {
        this.lastlogin = lastlogin;
    }

    public Integer getLogincount() {
        return logincount;
    }

    public void setLogincount(Integer logincount) {
        this.logincount = logincount;
    }

    private Long accountid;

    public Long getAccountid() {
        return this.accountid;
    }

    public void setAccountid(Long id) {
        this.accountid = id;
    }

    public int hashCode() {
        if (getAccountid() == null) {
            return -1;
        }
        return getAccountid().intValue();
    }

    public boolean equals(Object o) {
        return o != null && o instanceof Account && o.hashCode() == this.hashCode();
    }

    @Override
    public String toString() {
        return "Account [creationdate=" + creationdate + ", openbalance="
                + openbalance + ", logoutcount=" + logoutcount + ", balance="
                + balance + ", lastlogin=" + lastlogin + ", logincount="
                + logincount + ", accountid=" + accountid + "]";
    }

}
