package io.vulcan.bean.helper.translator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum DateTranslator implements Translator<Date> {

    INSTANCE;

    private final Logger log = LoggerFactory.getLogger(DateTranslator.class);

    private long toMillis(Object value) {

        if (value instanceof java.sql.Date) {
            final java.sql.Date javaDate = (java.sql.Date) value;
            return javaDate.getTime();
        }

        if (value instanceof java.sql.Time) {
            final java.sql.Time javaDate = (java.sql.Time) value;
            return javaDate.getTime();
        }

        if (value instanceof java.sql.Timestamp) {
            final java.sql.Timestamp javaDate = (java.sql.Timestamp) value;
            return javaDate.getTime();
        }

        // Handle Date (includes java.sql.Date & java.sql.Time)
        if (value instanceof Date) {
            return ((Date)value).getTime();
        }

        // Handle Calendar
        if (value instanceof Calendar) {
            final Calendar calendar = (Calendar)value;
            return calendar.getTime().getTime();
        }

        // Handle Long
        if (value instanceof Long) {
            return (Long)value;
        }

        if (value instanceof LocalDate) {
            final Date date = Date.from(Instant.from(((LocalDate) value).atStartOfDay(ZoneId.systemDefault())));
            return date.getTime();
        }

        if ((value instanceof LocalDateTime)) {
            final Date date = Date.from(Instant.from(((LocalDateTime) value).atZone(ZoneId.systemDefault())));
            return date.getTime();
        }

        if (value instanceof LocalTime) {
            final LocalDateTime ldt = ((LocalTime) value).atDate(LocalDate.now());
            final Instant inst = Instant.from(ldt);
            final Date date = Date.from(inst);
            return date.getTime();
        }

        log.warn("对象属性类型转换失败，{} 无法转换为 {}", value.getClass(), Date.class);
        return -1;
    }

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
        return toDate(toMillis(value));
    }

    @Override
    public Date translate(Object value) {
        if (value == null) {
            return null;
        }
        return toDate(value);
    }
}
