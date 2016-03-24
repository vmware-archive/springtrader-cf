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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.nanotrader.data.cloud.AccountDecoder;
import org.springframework.nanotrader.data.cloud.AccountEncoder;
import org.springframework.nanotrader.data.cloud.AccountProfileRepository;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Profile("cloud")
public class AccountProfileServiceImpl implements AccountProfileService {

    private static final Logger LOG = Logger.getLogger(AccountProfileServiceImpl.class);

    AccountProfileRepository accountProfileRepository;

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    String accountRepositoryName;

    @Override
    public void deletelAccountProfile(Accountprofile accountProfile) {
        accountProfileRepository().delete(accountProfile);
    }

    @Override
    public Accountprofile findAccountProfile(Long id) {
        return accountProfileRepository().findOne(id);
    }

    @Override
    public Accountprofile saveAccountProfile(Accountprofile accountProfile) {
        return accountProfileRepository().save(accountProfile);

    }

    @Override
    public Accountprofile findByUseridAndPasswd(String userId, String passwd) {
        List<Accountprofile> aps = accountProfileRepository()
                .findByUseridAndPasswd(userId, passwd);
        if (aps == null || aps.size() < 1) {
            return null;
        }
        return aps.get(0);
    }

    @Override
    public Accountprofile findByUserid(String username) {
        List<Accountprofile> aps = accountProfileRepository()
                .findByUserid(username);
        if (aps == null || aps.size() < 1) {
            return null;
        }
        return aps.get(0);
    }

    @Override
    public Accountprofile findByAuthtoken(String authtoken) {
        List<Accountprofile> aps = accountProfileRepository()
                .findByAuthtoken(authtoken);
        if (aps == null || aps.size() < 1) {
            return null;
        }
        return aps.get(0);
    }

    private AccountProfileRepository accountProfileRepository() {
        if (this.accountProfileRepository == null) {
            LOG.info("initializing accountProfileRepository named: " + accountRepositoryName);
            String url = discoveryClient.getNextServerFromEureka(
                    accountRepositoryName, false).getHomePageUrl();

            LOG.info("accountProfileRepository url is: " + url);

            this.accountProfileRepository = Feign.builder()
                    .encoder(new AccountEncoder())
                    .decoder(new AccountDecoder())
                    .target(AccountProfileRepository.class, url);

            LOG.info("accountRepository initialization complete.");
        }
        return this.accountProfileRepository;
    }

    @Override
    public Accountprofile login(String username, String password) {
        Accountprofile accountProfile = findByUseridAndPasswd(username, password);
        if (accountProfile != null) {
            accountProfile.setAuthtoken(UUID.randomUUID().toString());
            List<Account> accounts = accountProfile.getAccounts();
            for (Account account : accounts) {
                account.setLogincount(account.getLogincount() + 1);
                account.setLastlogin(new Date());
            }
            return saveAccountProfile(accountProfile); // persist new auth token
        }
        return null;
    }

    @Override
    public void logout(String authtoken) {
        Accountprofile accountProfile = findByAuthtoken(authtoken);
        if (accountProfile != null) {
            accountProfile.setAuthtoken(null); // remove token
            List<Account> accounts = accountProfile.getAccounts();
            for (Account account : accounts) {
                account.setLogoutcount(account.getLogoutcount() + 1);
            }
            saveAccountProfile(accountProfile);
        }
    }

    @Override
    public Accountprofile updateAccountProfile(Accountprofile accountProfile, String username) {
        Accountprofile accountProfileResponse = null;
        Accountprofile acctProfile = findByUserid(username);
        // make sure that the primary key hasn't been altered
        if (acctProfile != null) {
            accountProfile.setAuthtoken(acctProfile.getAuthtoken());
            accountProfileResponse = saveAccountProfile(accountProfile);
        }
        return accountProfileResponse;
    }
}
