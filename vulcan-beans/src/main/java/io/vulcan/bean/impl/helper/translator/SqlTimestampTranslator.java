package io.vulcan.bean.impl.helper.translator;

enum SqlTimestampTranslator implements Translator<java.sql.Timestamp> {

    INSTANCE;

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
        return toSqlTimestamp(Tools.toMillis(value));
    }

    @Override
    public java.sql.Timestamp translate(Object value) {
        if (value == null) {
            return null;
        }
        return toSqlTimestamp(value);
    }
}
