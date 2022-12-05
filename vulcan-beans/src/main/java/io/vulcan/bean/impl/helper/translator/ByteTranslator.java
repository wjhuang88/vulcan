package io.vulcan.bean.impl.helper.translator;

enum ByteTranslator implements Translator<Byte> {

    INSTANCE;

    @Override
    public Byte translate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Byte) {
            return (Byte) value;
        }

        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }

        if (value instanceof Boolean) {
            return (byte) ((Boolean) value ? 1 : 0);
        }

        if (value instanceof Character) {
            return (byte) Character.digit((Character) value, 10);
        }

        if (value instanceof String) {
            return Byte.parseByte((String)value);
        }
        return null;
    }
}
