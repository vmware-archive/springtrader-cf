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
import java.util.ArrayList;
import java.util.List;

public class Accountprofile implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long profileid;

    public Long getProfileid() {
        return this.profileid;
    }

    public void setProfileid(Long id) {
        this.profileid = id;
    }

    private List<Account> accounts;

    private String address;

    private String passwd;

    @NotNull
    private String userid;

    private String email;

    private String creditcard;

    private String fullname;


    private String authtoken;

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreditcard() {
        return creditcard;
    }

    public void setCreditcard(String creditcard) {
        this.creditcard = creditcard;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void addAccount(Account account) {
        if (getAccounts() == null || getAccounts().size() < 1) {
            setAccounts(new ArrayList<Account>());
        }
        account.setAccountprofile(this);
        getAccounts().add(account);
    }

    public int hashCode() {
        if (getProfileid() == null) {
            return -1;
        }
        return getProfileid().intValue();
    }

    public boolean equals(Object o) {
        return o != null && o instanceof Accountprofile && o.hashCode() == this.hashCode();
    }

    @Override
    public String toString() {
        return "Accountprofile [profileid=" + profileid + ", address=" + address + ", passwd=" + passwd + ", userid="
                + userid + ", email=" + email + ", creditcard=" + creditcard + ", fullname=" + fullname + "]";
    }
}
