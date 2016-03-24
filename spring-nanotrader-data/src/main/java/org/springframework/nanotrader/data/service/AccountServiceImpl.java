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
import org.springframework.nanotrader.data.cloud.AccountRepository;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.stereotype.Service;

@Service
@Profile("cloud")
public class AccountServiceImpl implements AccountService {

    private static final Logger LOG = Logger.getLogger(AccountServiceImpl.class);

    @Autowired
    DiscoveryClient discoveryClient;

    AccountRepository accountRepository;

    @Autowired
    String accountRepositoryName;

    @Autowired
    AccountProfileService accountProfileService;

    public void deleteAccount(Account account) {
        accountRepository().delete(account);
    }

    public Account findAccount(Long id) {
        return accountRepository().findOne(id);
    }

    public Account saveAccount(Account account) {
        Accountprofile ap = accountProfileService.findAccountProfile(account.getAccountprofile().getProfileid());
        if (ap.getAccounts() == null || ap.getAccounts().size() < 1) {
            ap.addAccount(account);
        } else {
            ap.getAccounts().remove(0);
            ap.getAccounts().add(account);
        }
        ap = accountProfileService.saveAccountProfile(ap);
        return ap.getAccounts().get(0);
    }

    @Override
    public Account findByProfile(Accountprofile accountprofile) {
        Accountprofile ap = accountProfileService.findAccountProfile(accountprofile
                .getProfileid());
        if (ap != null && ap.getAccounts() != null && ap.getAccounts().size() > 0) {
            return ap.getAccounts().get(0);
        }
        return null;
    }

    private AccountRepository accountRepository() {
        if (this.accountRepository == null) {
            LOG.info("initializing accountRepository named: " + accountRepositoryName);
            String url = discoveryClient.getNextServerFromEureka(
                    accountRepositoryName, false).getHomePageUrl();

            LOG.info("accountRepository url is: " + url);

            this.accountRepository = Feign.builder()
                    .encoder(new AccountEncoder())
                    .decoder(new AccountDecoder())
                    .target(AccountRepository.class, url);

            LOG.info("accountRepository initialization complete.");
        }
        return this.accountRepository;
    }
}
