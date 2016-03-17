package org.springframework.nanotrader.service;

import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.service.AccountService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FallBackAccountService implements AccountService {

    @Override
    public void deleteAccount(Account account) {
        // no-op
    }

    @Override
    public Account findAccount(Long id) {
        if (id != null) {
            for (Accountprofile ap : FallBackAccountProfileService.profiles.values()) {
                List<Account> as = ap.getAccounts();
                if (as != null) {
                    for (Account a : as) {
                        if (a.getAccountid().equals(id)) {
                            return a;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Account saveAccount(Account account) {
        if (account == null || account.getAccountprofile() == null) {
            return null;
        }
        if (account.getAccountid() == null) {
            account.setAccountid(UUID.randomUUID().getMostSignificantBits());
        }
        return account;
    }

    @Override
    public Account findByProfile(Accountprofile accountprofile) {
        if (accountprofile != null && accountprofile.getProfileid() != null) {

            List<Account> l = FallBackAccountProfileService.profiles.get(accountprofile.getProfileid()).getAccounts();
            if (l != null && l.size() > 0) {
                return l.get(0);
            }
        }

        return null;
    }

    static Account fakeAccount(Accountprofile profile) {
        Account a = new Account();
        a.setAccountid(-1L);
        a.setBalance(new BigDecimal(100));
        a.setCreationdate(new Date());
        a.setLastlogin((new Date()));
        a.setLogincount(1);
        a.setLogoutcount(0);
        a.setOpenbalance(new BigDecimal(100));
        a.setAccountprofile(profile);

        return a;
    }
}
