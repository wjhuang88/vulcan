package io.weijie.vulcan.bean.helper.translator;

import java.math.BigDecimal;
import java.math.BigInteger;

enum BigDecimalTranslator implements Translator<BigDecimal> {

    INSTANCE;

    @Override
    public BigDecimal translate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }

        if (value instanceof Integer
                || value instanceof Short
                || value instanceof Byte) {
            return new BigDecimal(((Number) value).intValue());
        }

        if (value instanceof Long) {
            return new BigDecimal(((Number) value).longValue());
        }

        if (value instanceof Double || value instanceof Float) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }

        if (value instanceof Boolean) {
            return ((Boolean) value ? BigDecimal.ONE : BigDecimal.ZERO);
        }

        if (value instanceof String) {
            return new BigDecimal((String) value);
        }
        return null;
    }
}
