package io.vulcan.bean.helper.translator;

import io.vulcan.datetime.DateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum SqlTimestampTranslator implements Translator<java.sql.Timestamp> {

    INSTANCE;

    private final Logger log = LoggerFactory.getLogger(SqlTimestampTranslator.class);

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
            Date date = DateTimeUtils.date((LocalDate) value);
            return date.getTime();
        }

        if ((value instanceof LocalDateTime)) {
            Date date = DateTimeUtils.date((LocalDateTime) value);
            return date.getTime();
        }

        if (value instanceof LocalTime) {
            Date date = DateTimeUtils.date(((LocalTime) value).atDate(DateTimeUtils.localDate(new Date())));
            return date.getTime();
        }

        log.warn("对象属性类型转换失败，{} 无法转换为 {}", value.getClass(), Date.class);
        return -1;
    }

    private java.sql.Timestamp toSqlTimestamp(long millis) {
        if (millis < 0) {
            return null;
        }
        return new java.sql.Timestamp(millis);
    }

    java.sql.Timestamp toSqlTimestamp(Object value) {
        if (value instanceof java.sql.Timestamp) {
            return (java.sql.Timestamp) value;
        }
        return toSqlTimestamp(toMillis(value));
    }

    @Override
    public java.sql.Timestamp translate(Object value) {
        if (value == null) {
            return null;
        }
        return toSqlTimestamp(value);
    }
}
