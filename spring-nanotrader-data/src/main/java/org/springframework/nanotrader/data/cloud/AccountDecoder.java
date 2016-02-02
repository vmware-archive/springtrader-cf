package org.springframework.nanotrader.data.cloud;

import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import feign.FeignException;
import feign.Response;
import feign.gson.GsonDecoder;
import net.minidev.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountDecoder extends GsonDecoder {

    private static final Type LIST_OF_ACCOUNT = new TypeToken<List<Account>>() {
    }.getType();

    private static final Type LIST_OF_ACCOUNTPROFILE = new TypeToken<List<Accountprofile>>() {
    }.getType();

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private static final Logger LOG = Logger.getLogger(AccountDecoder.class);

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {

        Response.Body body = response.body();
        if (body == null || body.length() == 0) {
            return null;
        }

        // System.out.println(Util.toString(body.asReader()));

        if (Account.class.equals(type)) {
            return accountFromJson(JsonPath.parse(body.asInputStream()));
        }

        if (LIST_OF_ACCOUNT.equals(type)) {
            return accountsFromJson(JsonPath.parse(body.asInputStream()));
        }

        if (Accountprofile.class.equals(type)) {
            return accountprofileFromJson(JsonPath.parse(body.asInputStream()), true);
        }

        if (LIST_OF_ACCOUNTPROFILE.equals(type)) {
            return accountprofilesFromJson(JsonPath.parse(body.asInputStream()));
        }

        return super.decode(response, type);
    }

    private Accountprofile accountprofileFromJson(ReadContext ctx, boolean processAccounts) {
        if (ctx.json().toString().length() < 1) {
            return null;
        }

        Accountprofile ap = new Accountprofile();
        ap.setAddress(getStringValue(ctx, "$.address"));
        ap.setAuthtoken(getStringValue(ctx, "$.authToken"));
        ap.setCreditcard(getStringValue(ctx, "$.creditCard"));
        ap.setEmail(getStringValue(ctx, "$.email"));
        ap.setFullname(getStringValue(ctx, "$.fullName"));
        ap.setPasswd(getStringValue(ctx, "$.passwd"));
        ap.setProfileid(getLongValue(ctx, "$.accountProfileId"));
        ap.setUserid(getStringValue(ctx, "$.userId"));

        JSONArray accounts = ctx.read("$.accounts");
        if (accounts != null && processAccounts) {
            ap.setAccounts(accountsFromJson(JsonPath.parse(accounts.toString())));
            for (Account a : ap.getAccounts()) {
                a.setAccountprofile(ap);
            }
        }

        return ap;
    }

    private List<Accountprofile> accountprofilesFromJson(ReadContext ctx) {
        ArrayList<Accountprofile> accountprofiles = new ArrayList<Accountprofile>();

        JSONArray as = ctx.read("$");
        for (int i = 0; i < as.size(); i++) {
            Accountprofile ap = new Accountprofile();
            ap.setAddress(getStringValue(ctx, "$.[" + i + "].address"));
            ap.setAuthtoken(getStringValue(ctx, "$.[" + i + "].authToken"));
            ap.setCreditcard(getStringValue(ctx, "$.[" + i + "].creditCard"));
            ap.setEmail(getStringValue(ctx, "$.[" + i + "].email"));
            ap.setFullname(getStringValue(ctx, "$.[" + i + "].fullName"));
            ap.setPasswd(getStringValue(ctx, "$.[" + i + "].passwd"));
            ap.setProfileid(getLongValue(ctx, "$.[" + i + "].accountProfileId"));
            ap.setUserid(getStringValue(ctx, "$.[" + i + "].userId"));

            accountprofiles.add(ap);

            JSONArray accounts = ctx.read("$.[" + i + "].accounts");
            if (accounts != null) {
                ap.setAccounts(accountsFromJson(JsonPath.parse(accounts.toString())));
            }
        }

        return accountprofiles;
    }

    private String getStringValue(ReadContext ctx, String path) {
        return ctx.read(path);
    }

    private Long getLongValue(ReadContext ctx, String path) {
        Object o = ctx.read(path);
        if (o == null) {
            return 0L;
        }
        return Long.decode(o.toString());
    }

    private Integer getIntegerValue(ReadContext ctx, String path) {
        Object o = ctx.read(path);
        if (o == null) {
            return 0;
        }
        return Integer.decode(o.toString());
    }

    private BigDecimal getBigDecimalValue(ReadContext ctx, String path) {
        Object o = ctx.read(path);
        if (o == null) {
            return new BigDecimal(0);
        }
        return new BigDecimal(o.toString());
    }

    private Date getDateValue(ReadContext ctx, String path) {
        Object o = ctx.read(path);
        if (o == null) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(o.toString());
        } catch (ParseException e) {
            LOG.error("unable to parse date string: " + o.toString(), e);
            return null;
        }
    }

    private Account accountFromJson(ReadContext ctx) {
        Account a = new Account();
        a.setAccountid(getLongValue(ctx, "$.accountId"));
        a.setBalance(getBigDecimalValue(ctx, "$.balance"));
        a.setCreationdate(getDateValue(ctx, "$.creationDate"));
        a.setLastlogin(getDateValue(ctx, "$.lastLogin"));
        a.setLogincount(getIntegerValue(ctx, "$.loginCount"));
        a.setLogoutcount(getIntegerValue(ctx, "$.logoutCount"));
        a.setOpenbalance(getBigDecimalValue(ctx, "$.openBalance"));

        Object ap = ctx.read("$.accountProfile");
        if (ap != null) {
            a.setAccountprofile(accountprofileFromJson(JsonPath.parse(ap.toString()), false));
        }

        return a;
    }

    private List<Account> accountsFromJson(ReadContext ctx) {
        ArrayList<Account> accounts = new ArrayList<Account>();

        JSONArray as = ctx.read("$");
        for (int i = 0; i < as.size(); i++) {
            Account a = new Account();
            a.setAccountid(getLongValue(ctx, "$.[" + i + "].accountId"));
            a.setBalance(getBigDecimalValue(ctx, "$.[" + i + "].balance"));
            a.setCreationdate(getDateValue(ctx, "$.[" + i + "].creationDate"));
            a.setLastlogin(getDateValue(ctx, "$.[" + i + "].lastLogin"));
            a.setLogincount(getIntegerValue(ctx, "$.[" + i + "].loginCount"));
            a.setLogoutcount(getIntegerValue(ctx, "$.[" + i + "].logoutCount"));
            a.setOpenbalance(getBigDecimalValue(ctx, "$.[" + i
                    + "].openBalance"));

            accounts.add(a);
        }

        return accounts;
    }
}