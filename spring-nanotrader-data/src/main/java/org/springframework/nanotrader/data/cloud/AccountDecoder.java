package org.springframework.nanotrader.data.cloud;

import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import feign.FeignException;
import feign.Response;
import feign.gson.GsonDecoder;
import net.minidev.json.JSONArray;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AccountDecoder extends GsonDecoder {

    private static final Type LIST_OF_ACCOUNT = new TypeToken<List<Account>>() {
    }.getType();

    private static final Type LIST_OF_ACCOUNTPROFILE = new TypeToken<List<Accountprofile>>() {
    }.getType();

    private final JsonUtils jsonUtils = new JsonUtils();

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
        ap.setAddress(jsonUtils.getStringValue(ctx, "$.address"));
        ap.setAuthtoken(jsonUtils.getStringValue(ctx, "$.authToken"));
        ap.setCreditcard(jsonUtils.getStringValue(ctx, "$.creditCard"));
        ap.setEmail(jsonUtils.getStringValue(ctx, "$.email"));
        ap.setFullname(jsonUtils.getStringValue(ctx, "$.fullName"));
        ap.setPasswd(jsonUtils.getStringValue(ctx, "$.passwd"));
        ap.setProfileid(jsonUtils.getLongValue(ctx, "$.accountProfileId"));
        ap.setUserid(jsonUtils.getStringValue(ctx, "$.userId"));

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
            ap.setAddress(jsonUtils.getStringValue(ctx, "$.[" + i + "].address"));
            ap.setAuthtoken(jsonUtils.getStringValue(ctx, "$.[" + i + "].authToken"));
            ap.setCreditcard(jsonUtils.getStringValue(ctx, "$.[" + i + "].creditCard"));
            ap.setEmail(jsonUtils.getStringValue(ctx, "$.[" + i + "].email"));
            ap.setFullname(jsonUtils.getStringValue(ctx, "$.[" + i + "].fullName"));
            ap.setPasswd(jsonUtils.getStringValue(ctx, "$.[" + i + "].passwd"));
            ap.setProfileid(jsonUtils.getLongValue(ctx, "$.[" + i + "].accountProfileId"));
            ap.setUserid(jsonUtils.getStringValue(ctx, "$.[" + i + "].userId"));

            accountprofiles.add(ap);

            JSONArray accounts = ctx.read("$.[" + i + "].accounts");
            if (accounts != null) {
                ap.setAccounts(accountsFromJson(JsonPath.parse(accounts.toString())));
            }
        }

        return accountprofiles;
    }

    private Account accountFromJson(ReadContext ctx) {
        Account a = new Account();
        a.setAccountid(jsonUtils.getLongValue(ctx, "$.accountId"));
        a.setBalance(jsonUtils.getBigDecimalValue(ctx, "$.balance"));
        a.setCreationdate(jsonUtils.getDateValue(ctx, "$.creationDate"));
        a.setLastlogin(jsonUtils.getDateValue(ctx, "$.lastLogin"));
        a.setLogincount(jsonUtils.getIntegerValue(ctx, "$.loginCount"));
        a.setLogoutcount(jsonUtils.getIntegerValue(ctx, "$.logoutCount"));
        a.setOpenbalance(jsonUtils.getBigDecimalValue(ctx, "$.openBalance"));

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
            a.setAccountid(jsonUtils.getLongValue(ctx, "$.[" + i + "].accountId"));
            a.setBalance(jsonUtils.getBigDecimalValue(ctx, "$.[" + i + "].balance"));
            a.setCreationdate(jsonUtils.getDateValue(ctx, "$.[" + i + "].creationDate"));
            a.setLastlogin(jsonUtils.getDateValue(ctx, "$.[" + i + "].lastLogin"));
            a.setLogincount(jsonUtils.getIntegerValue(ctx, "$.[" + i + "].loginCount"));
            a.setLogoutcount(jsonUtils.getIntegerValue(ctx, "$.[" + i + "].logoutCount"));
            a.setOpenbalance(jsonUtils.getBigDecimalValue(ctx, "$.[" + i
                    + "].openBalance"));

            accounts.add(a);
        }

        return accounts;
    }
}