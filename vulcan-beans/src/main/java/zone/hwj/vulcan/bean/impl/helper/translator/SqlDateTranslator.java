package zone.hwj.vulcan.bean.impl.helper.translator;

enum SqlDateTranslator implements Translator<java.sql.Date> {

    INSTANCE;

    private java.sql.Date toSqlDate(long millis) {
        if (millis < 0) {
            return null;
        }
        return new java.sql.Date(millis);
    }

    java.sql.Date toSqlDate(Object value) {
        if (value instanceof java.sql.Date) {
            return (java.sql.Date) value;
        }
        return toSqlDate(Tools.toMillis(value));
    }

    @Override
    public java.sql.Date translate(Object value) {
        if (value == null) {
            return null;
        }
        return toSqlDate(value);
    }
}
