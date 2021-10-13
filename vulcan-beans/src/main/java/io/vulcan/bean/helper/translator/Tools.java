package io.vulcan.bean.helper.translator;

import java.lang.reflect.Method;

public final class Tools {

    public static Method getTranslateMethod(final Class<?> methodClass) {

        final Class<?>[] params;
        final String translateMethod;
        if (methodClass == byte.class) {
            translateMethod = "translateByte";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == short.class) {
            translateMethod = "translateShort";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == int.class) {
            translateMethod = "translateInt";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == long.class) {
            translateMethod = "translateLong";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == float.class) {
            translateMethod = "translateFloat";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == double.class) {
            translateMethod = "translateDouble";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == boolean.class) {
            translateMethod = "translateBoolean";
            params = new Class<?>[]{Object.class};
        } else if (methodClass == char.class) {
            translateMethod = "translateChar";
            params = new Class<?>[]{Object.class};
        } else {
            translateMethod = "translate";
            params = new Class<?>[]{Class.class, Object.class};
        }
        try {
            return Translators.class.getMethod(translateMethod, params);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Invalid translator instance", e);
        }
    }
}
