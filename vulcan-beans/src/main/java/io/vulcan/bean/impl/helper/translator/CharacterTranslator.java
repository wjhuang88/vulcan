package io.vulcan.bean.impl.helper.translator;

enum CharacterTranslator implements Translator<Character> {

    INSTANCE;

    @Override
    public Character translate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Character) {
            return (Character) value;
        }

        if (value instanceof Number) {
            return Character.forDigit(((Number) value).intValue(), 10);
        }

        if (value instanceof Boolean) {
            return ((Boolean) value ? '1' : '0');
        }

        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.isEmpty()) {
                return '\0';
            }
            return strValue.charAt(0);
        }
        return null;
    }
}
