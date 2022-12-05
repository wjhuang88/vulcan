package io.vulcan.bean.impl.helper.translator;

enum DoubleTranslator implements Translator<Double> {

    INSTANCE;

    @Override
    public Double translate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Double) {
            return (Double) value;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean) value ? 1D : 0D);
        }

        if (value instanceof Character) {
            return (double) Character.digit((Character) value, 10);
        }

        if (value instanceof String) {
            return Double.parseDouble((String)value);
        }
        return null;
    }
}
