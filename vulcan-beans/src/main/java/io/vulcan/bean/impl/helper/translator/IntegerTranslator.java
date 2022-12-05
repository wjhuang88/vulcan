package io.vulcan.bean.impl.helper.translator;

enum IntegerTranslator implements Translator<Integer> {

    INSTANCE;

    @Override
    public Integer translate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean) value ? 1 : 0);
        }

        if (value instanceof Character) {
            return Character.digit((Character) value, 10);
        }

        if (value instanceof String) {
            return Integer.parseInt((String)value);
        }
        return null;
    }
}
