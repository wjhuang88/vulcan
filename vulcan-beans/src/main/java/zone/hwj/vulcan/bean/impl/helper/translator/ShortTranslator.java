package zone.hwj.vulcan.bean.impl.helper.translator;

enum ShortTranslator implements Translator<Short> {

    INSTANCE;

    @Override
    public Short translate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Short) {
            return (Short) value;
        }

        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }

        if (value instanceof Boolean) {
            return (short) ((Boolean) value ? 1 : 0);
        }

        if (value instanceof Character) {
            return (short) Character.digit((Character) value, 10);
        }

        if (value instanceof String) {
            return Short.parseShort((String)value);
        }
        return null;
    }
}
