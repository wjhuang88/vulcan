package io.vulcan.bean.impl.helper.translator;

import java.util.Date;

enum DateTranslator implements Translator<Date> {

    INSTANCE;

    private Date toDate(long millis) {
        if (millis < 0) {
            return null;
        }
        return new Date(millis);
    }

    public Date toDate(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        }
        return toDate(Tools.toMillis(value));
    }

    @Override
    public Date translate(Object value) {
        if (value == null) {
            return null;
        }
        return toDate(value);
    }
}
