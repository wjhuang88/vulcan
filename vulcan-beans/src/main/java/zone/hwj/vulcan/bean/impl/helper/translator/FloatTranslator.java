package zone.hwj.vulcan.bean.impl.helper.translator;

enum FloatTranslator implements Translator<Float> {

    INSTANCE;

    @Override
    public Float translate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Float) {
            return (Float) value;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean) value ? 1F : 0F);
        }

        if (value instanceof Character) {
            return (float) Character.digit((Character) value, 10);
        }

        if (value instanceof String) {
            return Float.parseFloat((String)value);
        }
        return null;
    }
}
