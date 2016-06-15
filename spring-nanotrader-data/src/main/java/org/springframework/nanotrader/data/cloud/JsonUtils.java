package org.springframework.nanotrader.data.cloud;

import com.jayway.jsonpath.ReadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;


class JsonUtils {

    private static final Logger LOG = LogManager.getLogger(OrderDecoder.class);

    String getStringValue(ReadContext ctx, String path) {
        return ctx.read(path);
    }

    Long getLongValue(ReadContext ctx, String path) {
        Object o = ctx.read(path);
        if (isEmpty(o)) {
            return null;
        }
        return Long.decode(o.toString());
    }

    BigDecimal getBigDecimalValue(ReadContext ctx, String path) {
        Object o = ctx.read(path);
        if (isEmpty(o)) {
            return null;
        }
        return new BigDecimal(o.toString());
    }

    Date getDateValue(ReadContext ctx, String path) {
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

    Integer getIntegerValue(ReadContext ctx, String path) {
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
