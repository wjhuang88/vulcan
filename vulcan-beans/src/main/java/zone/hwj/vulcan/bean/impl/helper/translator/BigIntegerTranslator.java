package zone.hwj.vulcan.bean.impl.helper.translator;

import java.math.BigDecimal;
import java.math.BigInteger;

enum BigIntegerTranslator implements Translator<BigInteger> {

    INSTANCE;

    @Override
    public BigInteger translate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toBigInteger();
        }

        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }

        if (value instanceof Number) {
            return BigInteger.valueOf(((Number) value).longValue());
        }

        if (value instanceof Boolean) {
            return ((Boolean) value ? BigInteger.ONE : BigInteger.ZERO);
        }

        if (value instanceof String) {
            return new BigInteger((String) value);
        }
        return null;
    }
}
