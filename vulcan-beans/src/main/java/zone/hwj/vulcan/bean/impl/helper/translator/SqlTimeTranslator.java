package zone.hwj.vulcan.bean.impl.helper.translator;

enum SqlTimeTranslator implements Translator<java.sql.Time> {

    INSTANCE;

    private java.sql.Time toSqlTime(long millis) {
        if (millis < 0) {
            return null;
        }
        return new java.sql.Time(millis);
    }

    java.sql.Time toSqlTime(Object value) {
        if (value instanceof java.sql.Time) {
            return (java.sql.Time) value;
        }
        return toSqlTime(Tools.toMillis(value));
    }

    @Override
    public java.sql.Time translate(Object value) {
        return toSqlTime(value);
    }
}
