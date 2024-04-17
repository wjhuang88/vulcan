package io.vulcan.bean.impl.helper.translator;

enum LongTranslator implements Translator<Long> {

    INSTANCE;

    @Override
    public Long translate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Long) {
            return (Long) value;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean) value ? 1L : 0L);
        }

        if (value instanceof Character) {
            return (long) Character.digit((Character) value, 10);
        }

        if (value instanceof String) {
            return Long.parseLong((String)value);
        }
        return null;
    }
}
