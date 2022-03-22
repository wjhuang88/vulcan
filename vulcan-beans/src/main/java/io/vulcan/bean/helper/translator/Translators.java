package io.vulcan.bean.helper.translator;

import io.vulcan.api.convertible.Copiable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public enum Translators {

    INSTANCE;

    private final Map<Class<?>, Translator<?>> translatorMap = new HashMap<>();

    Translators() { // 装载内置类型转换器
        translatorMap.put(Date.class, DateTranslator.INSTANCE);
        translatorMap.put(java.sql.Date.class, SqlDateTranslator.INSTANCE);
        translatorMap.put(java.sql.Time.class, SqlTimeTranslator.INSTANCE);
        translatorMap.put(java.sql.Timestamp.class, SqlTimestampTranslator.INSTANCE);
        translatorMap.put(BigDecimal.class, BigDecimalTranslator.INSTANCE);
        translatorMap.put(BigInteger.class, BigIntegerTranslator.INSTANCE);
        translatorMap.put(String.class, StringTranslator.INSTANCE);
    }

    /**
     * 引用类型转换方法
     *
     * @param clazz 目标类型
     * @param value 需转换的对象
     * @param <T> 目标类型
     * @return 转换结果
     */
    @SuppressWarnings("unchecked")
    public <T> T translate(final Class<T> clazz, Object value) {
        if (value instanceof Copiable) {
            value = ((Copiable<?>) value).copy();
        }
        final Translator<T> translator = (Translator<T>) translatorMap.get(clazz);
        if (translator != null) {
            return translator.translate(value);
        }
        return (T) value;
    }

    /**
     * char转换方法
     *
     * @param value 需转换的对象
     * @return 转换结果
     */
    public char translateChar(Object value) {
        if (value == null) {
            return 0;
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

        return (char) value;
    }

    /**
     * byte转换方法
     *
     * @param value 需转换的对象
     * @return 转换结果
     */
    public byte translateByte(Object value) {
        if (value == null) {
            return 0;
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

        return (byte) value;
    }

    /**
     * short转换方法
     *
     * @param value 需转换的对象
     * @return 转换结果
     */
    public short translateShort(Object value) {
        if (value == null) {
            return 0;
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

        return (short) value;
    }

    /**
     * int转换方法
     *
     * @param value 需转换的对象
     * @return 转换结果
     */
    public int translateInt(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }

        if (value instanceof Character) {
            return Character.digit((Character) value, 10);
        }

        if (value instanceof String) {
            return Integer.parseInt((String)value);
        }

        return (int) value;
    }

    /**
     * long转换方法
     *
     * @param value 需转换的对象
     * @return 转换结果
     */
    public long translateLong(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }

        if (value instanceof Character) {
            return Character.digit((Character) value, 10);
        }

        if (value instanceof String) {
            return Long.parseLong((String)value);
        }

        return (long) value;
    }

    /**
     * float转换方法
     *
     * @param value 需转换的对象
     * @return 转换结果
     */
    public float translateFloat(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof Boolean) {
            return (Boolean) value ? 1F : 0F;
        }

        if (value instanceof Character) {
            return (float) Character.digit((Character) value, 10);
        }

        if (value instanceof String) {
            return Float.parseFloat((String)value);
        }

        return (float) value;
    }

    /**
     * double转换方法
     *
     * @param value 需转换的对象
     * @return 转换结果
     */
    public double translateDouble(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean) value ? 1D : 0D);
        }

        if (value instanceof Character) {
            return Character.digit((Character) value, 10);
        }

        if (value instanceof String) {
            return Double.parseDouble((String)value);
        }

        return (double) value;
    }

    /**
     * boolean转换方法
     *
     * @param value 需转换的对象
     * @return 转换结果
     */
    public boolean translateBoolean(Object value) {
        if (value == null) {
            return false;
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

        return (boolean) value;
    }
}
