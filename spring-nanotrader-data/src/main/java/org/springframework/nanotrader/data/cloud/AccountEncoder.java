package org.springframework.nanotrader.data.cloud;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import feign.RequestTemplate;
import feign.gson.GsonEncoder;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountEncoder extends GsonEncoder {

    private static final Type MAP_OF_STRING_OBJECT = new TypeToken<Map<String, Object>>() {
    }.getType();

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private final Gson gson = new Gson();

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) {

        Object o = null;
        if (bodyType.equals(Accountprofile.class)) {
            o = processAccountprofile((Accountprofile) object, true);
        }

        if (bodyType.equals(Account.class)) {
            o = processAccount((Account) object, true);
        }

        template.body(gson.toJson(o, MAP_OF_STRING_OBJECT));
    }

    private Object processAccountprofile(Accountprofile ap, boolean processAccounts) {
        if (ap == null) {
            return null;
        }

        Map<String, Object> m = new HashMap<String, Object>();
        m.put("address", ap.getAddress());
        m.put("authToken", ap.getAuthtoken());
        m.put("creditCard", ap.getCreditcard());
        m.put("email", ap.getEmail());
        m.put("fullName", ap.getFullname());
        m.put("passwd", ap.getPasswd());
        m.put("accountProfileId", ap.getProfileid());
        m.put("userId", ap.getUserid());

        if (ap.getAccounts() != null && ap.getAccounts().size() > 0 && processAccounts) {
            List<Object> l = new ArrayList<Object>();
            for (Account a : ap.getAccounts()) {
                l.add(processAccount(a, false));
            }
            m.put("accounts", l);
        }

        return m;
    }

    private Object processAccount(Account a, boolean processProfile) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("accountId", a.getAccountid());
        m.put("balance", a.getBalance());
        if (a.getCreationdate() != null) {
            m.put("creationDate", DATE_FORMAT.format(a.getCreationdate()));
        }

        if (a.getLastlogin() != null) {
            m.put("lastLogin", DATE_FORMAT.format(a.getCreationdate()));
        }

        m.put("loginCount", a.getLogincount());
        m.put("logoutCount", a.getLogoutcount());
        m.put("openBalance", a.getOpenbalance());

        if (a.getAccountprofile() != null && processProfile) {
            m.put("accountProfile",
                    processAccountprofile(a.getAccountprofile(), false));
        }

        return m;
    }
}