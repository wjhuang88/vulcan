package io.vulcan.bean.impl.helper.translator;

enum BooleanTranslator implements Translator<Boolean> {

    INSTANCE;

    @Override
    public Boolean translate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Character) {
            return Character.digit((Character) value, 10) != 0;
        }

        if (value instanceof String) {
            return Boolean.parseBoolean(((String) value).toLowerCase());
        }
        return null;
    }
}
