package org.springframework.nanotrader.service;

import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FallBackAccountProfileService implements AccountProfileService {

    static final Map<Long, Accountprofile> profiles = new HashMap<Long, Accountprofile>();

    @Override
    public void deletelAccountProfile(Accountprofile accountProfile) {
        if (accountProfile != null && profiles.containsKey(accountProfile.getProfileid())) {
            profiles.remove(accountProfile.getProfileid());
        }
    }

    @Override
    public Accountprofile findAccountProfile(Long id) {
        if (id == null) {
            return null;
        }
        return profiles.get(id);
    }

    @Override
    public Accountprofile saveAccountProfile(Accountprofile accountProfile) {
        if (accountProfile != null) {
            if (accountProfile.getProfileid() == null) {
                accountProfile.setProfileid(UUID.randomUUID().getMostSignificantBits());
            }
            profiles.put(accountProfile.getProfileid(), accountProfile);
        }
        return accountProfile;
    }

    @Override
    public Accountprofile findByUserid(String userId) {
        if (userId == null) {
            return null;
        }
        for (Accountprofile ap : profiles.values()) {
            if (ap.getUserid().equals(userId)) {
                return ap;
            }
        }
        return null;
    }

    @Override
    public Accountprofile findByUseridAndPasswd(String userId, String passwd) {
        if (userId == null || passwd == null) {
            return null;
        }
        for (Accountprofile ap : profiles.values()) {
            if (ap.getUserid().equals(userId) && ap.getPasswd().equals(passwd)) {
                return ap;
            }
        }
        return null;
    }

    @Override
    public Accountprofile findByAuthtoken(String authtoken) {
        if (authtoken == null) {
            return null;
        }
        for (Accountprofile ap : profiles.values()) {
            if (ap.getAuthtoken().equals(authtoken)) {
                return ap;
            }
        }
        return null;
    }

    public static Accountprofile fakeAccountProfile(boolean includeAccount) {
        Accountprofile ap = new Accountprofile();

        ap.setAddress("fake address");
        ap.setAuthtoken("invalid authtoken");
        ap.setCreditcard("invalid creditcard");
        ap.setEmail("fake email");
        ap.setFullname("Fake Faker");
        ap.setPasswd("fake");
        ap.setProfileid(-1L);
        ap.setUserid("fake");

        if (includeAccount) {
            List<Account> accounts = new ArrayList<Account>();
            accounts.add(FallBackAccountService.fakeAccount(ap));
            ap.setAccounts(accounts);
        }

        return ap;
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
