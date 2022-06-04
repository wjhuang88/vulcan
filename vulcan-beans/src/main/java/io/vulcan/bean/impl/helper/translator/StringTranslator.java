package io.vulcan.bean.impl.helper.translator;

enum StringTranslator implements Translator<String> {

    INSTANCE;

    @Override
    public String translate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }
}
