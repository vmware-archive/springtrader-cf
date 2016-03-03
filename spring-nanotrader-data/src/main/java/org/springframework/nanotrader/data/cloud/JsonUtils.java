package org.springframework.nanotrader.data.cloud;

import com.jayway.jsonpath.ReadContext;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jgordon on 3/3/16.
 */
public class JsonUtils {

    private static final Logger LOG = Logger.getLogger(OrderDecoder.class);

    public String getStringValue(ReadContext ctx, String path) {
        return ctx.read(path);
    }

    public Long getLongValue(ReadContext ctx, String path) {
        Object o = ctx.read(path);
        if (isEmpty(o)) {
            return null;
        }
        return Long.decode(o.toString());
    }

    public BigDecimal getBigDecimalValue(ReadContext ctx, String path) {
        Object o = ctx.read(path);
        if (isEmpty(o)) {
            return null;
        }
        return new BigDecimal(o.toString());
    }

    public Date getDateValue(ReadContext ctx, String path) {
        Object o = ctx.read(path);
        if (isEmpty(o)) {
            return null;
        }
        try {
            DateTimeFormatter isoDateFormat = ISODateTimeFormat.dateTime();
            return DateTime.parse(o.toString(), isoDateFormat).toDate();
        } catch (Exception e) {
            LOG.error("unable to parse date string: " + o.toString(), e);
            return null;
        }
    }

    public Integer getIntegerValue(ReadContext ctx, String path) {
        Object o = ctx.read(path);
        if (isEmpty(o)) {
            return null;
        }
        return Integer.decode(o.toString());
    }

    private boolean isEmpty(Object o) {
        return o == null || o.toString().length() < 1;
    }
}
