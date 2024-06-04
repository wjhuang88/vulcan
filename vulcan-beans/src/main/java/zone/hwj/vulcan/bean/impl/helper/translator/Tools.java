package zone.hwj.vulcan.bean.impl.helper.translator;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Calendar;
import java.util.Date;

public final class Tools {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'][' '][HH:mm:ss]");
    private static final DateTimeFormatter DEFAULT_FORMATTER_WRITE = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'][HH:mm:ss]");
    private static final TemporalQuery<Instant> DEFAULT_QUERY = (TemporalAccessor temporal) -> {
        if (temporal.isSupported(ChronoField.HOUR_OF_DAY)) {
            return Instant.from(LocalDateTime.from(temporal).atZone(ZoneId.systemDefault()));
        }
        return Instant.from(LocalDate.from(temporal).atStartOfDay(ZoneId.systemDefault()));
    };

    public static Method getTranslateMethod(final Class<?> methodClass) {

        final Class<?>[] params;
        final String translateMethod;
        if (methodClass == byte.class) {
            translateMethod = "translateByte";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == short.class) {
            translateMethod = "translateShort";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == int.class) {
            translateMethod = "translateInt";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == long.class) {
            translateMethod = "translateLong";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == float.class) {
            translateMethod = "translateFloat";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == double.class) {
            translateMethod = "translateDouble";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == boolean.class) {
            translateMethod = "translateBoolean";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == char.class) {
            translateMethod = "translateChar";
            params = new Class<?>[]{Object.class};
        } else {
            translateMethod = "translate";
            params = new Class<?>[]{Class.class, Object.class};
        }
        try {
            return Translators.class.getMethod(translateMethod, params);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Invalid translator instance", e);
        }
    }

    static long toMillis(Object value) {

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
            final Instant inst = Instant.from(ldt.atZone(ZoneId.systemDefault()));
            return inst.toEpochMilli();
        }

        if (value instanceof String) {
            if (((String) value).isEmpty()) {
                return -1;
            }
            final Instant inst = DEFAULT_FORMATTER.parse((String) value, DEFAULT_QUERY);
            return inst.toEpochMilli();
        }

        return -1;
    }

    static String dateFormat(Object value) {
        if (value instanceof Number || value instanceof String) {
            return value.toString();
        }

        final long millis = toMillis(value);
        if (millis == -1) {
            return value.toString();
        }

        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());

        return DEFAULT_FORMATTER_WRITE.format(zonedDateTime);
    }
}
