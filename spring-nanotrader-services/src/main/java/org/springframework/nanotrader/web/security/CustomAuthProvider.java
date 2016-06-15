package org.springframework.nanotrader.web.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.nanotrader.data.service.AccountProfileService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthProvider implements AuthenticationProvider {

    private static Logger LOG = LogManager.getLogger(CustomAuthProvider.class);

    @Autowired
    private AccountProfileService accountProfileService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        Accountprofile accountProfile = accountProfileService.login(name, password);
        if (accountProfile == null) {
            LOG.error("accountProfile not found for user=" + name);
            throw new org.springframework.nanotrader.service.support.exception.AuthenticationException("User not found");
        }

        if(accountProfile.getAccounts() == null || accountProfile.getAccounts().size() < 1) {
            LOG.error("accountProfile has no accounts for user=" + name);
            throw new org.springframework.nanotrader.service.support.exception.AuthenticationException("User has no accounts");
        }

        //assume we're using the first account....
        Long accountId = accountProfile.getAccounts().get(0).getAccountid();

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_API_USER"));

        User user = new CustomUser(accountProfile.getUserid(), accountProfile.getPasswd(), grantedAuths, accountId, accountProfile.getProfileid());

        return new UsernamePasswordAuthenticationToken(user, password, grantedAuths);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
