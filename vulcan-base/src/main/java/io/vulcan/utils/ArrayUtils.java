package io.vulcan.utils;

import java.lang.reflect.Array;

public class ArrayUtils {

    public static int getLength(final Object array) {
        if (null == array) {
            return 0;
        }
        return Array.getLength(array);
    }

    public static boolean[] clone(final boolean[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }

    public static byte[] clone(final byte[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }

    public static char[] clone(final char[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }

    public static double[] clone(final double[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }

    public static float[] clone(final float[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }

    public static int[] clone(final int[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }

    public static long[] clone(final long[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }

    public static short[] clone(final short[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }

    public static <T> T[] clone(final T[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }

    public static boolean[] append(final boolean[] array, final boolean... elements) {
        final int additionLen = getLength(elements);
        if (additionLen == 0) {
            return clone(array);
        }
        final int originLen = getLength(array);
        final boolean[] newArray = (boolean[]) growArray(array, Boolean.TYPE, additionLen);
        if (additionLen == 1) {
            newArray[newArray.length - 1] = elements[0];
        } else {
            System.arraycopy(elements, 0, newArray, originLen, additionLen);
        }
        return newArray;
    }

    public static byte[] append(final byte[] array, final byte... elements) {
        final int additionLen = getLength(elements);
        if (additionLen == 0) {
            return clone(array);
        }
        final int originLen = getLength(array);
        final byte[] newArray = (byte[]) growArray(array, Byte.TYPE, additionLen);
        if (additionLen == 1) {
            newArray[newArray.length - 1] = elements[0];
        } else {
            System.arraycopy(elements, 0, newArray, originLen, additionLen);
        }
        return newArray;
    }

    public static char[] append(final char[] array, final char... elements) {
        final int additionLen = getLength(elements);
        if (additionLen == 0) {
            return clone(array);
        }
        final int originLen = getLength(array);
        final char[] newArray = (char[]) growArray(array, Character.TYPE, additionLen);
        if (additionLen == 1) {
            newArray[newArray.length - 1] = elements[0];
        } else {
            System.arraycopy(elements, 0, newArray, originLen, additionLen);
        }
        return newArray;
    }

    public static double[] append(final double[] array, final double... elements) {
        final int additionLen = getLength(elements);
        if (additionLen == 0) {
            return clone(array);
        }
        final int originLen = getLength(array);
        final double[] newArray = (double[]) growArray(array, Double.TYPE, additionLen);
        if (additionLen == 1) {
            newArray[newArray.length - 1] = elements[0];
        } else {
            System.arraycopy(elements, 0, newArray, originLen, additionLen);
        }
        return newArray;
    }

    public static float[] append(final float[] array, final float... elements) {
        final int additionLen = getLength(elements);
        if (additionLen == 0) {
            return clone(array);
        }
        final int originLen = getLength(array);
        final float[] newArray = (float[]) growArray(array, Float.TYPE, additionLen);
        if (additionLen == 1) {
            newArray[newArray.length - 1] = elements[0];
        } else {
            System.arraycopy(elements, 0, newArray, originLen, additionLen);
        }
        return newArray;
    }

    public static int[] append(final int[] array, final int... elements) {
        final int additionLen = getLength(elements);
        if (additionLen == 0) {
            return clone(array);
        }
        final int originLen = getLength(array);
        final int[] newArray = (int[]) growArray(array, Integer.TYPE, additionLen);
        if (additionLen == 1) {
            newArray[newArray.length - 1] = elements[0];
        } else {
            System.arraycopy(elements, 0, newArray, originLen, additionLen);
        }
        return newArray;
    }

    public static long[] append(final long[] array, final long... elements) {
        final int additionLen = getLength(elements);
        if (additionLen == 0) {
            return clone(array);
        }
        final int originLen = getLength(array);
        final long[] newArray = (long[]) growArray(array, Long.TYPE, additionLen);
        if (additionLen == 1) {
            newArray[newArray.length - 1] = elements[0];
        } else {
            System.arraycopy(elements, 0, newArray, originLen, additionLen);
        }
        return newArray;
    }

    public static short[] append(final short[] array, final short... elements) {
        final int additionLen = getLength(elements);
        if (additionLen == 0) {
            return clone(array);
        }
        final int originLen = getLength(array);
        final short[] newArray = (short[]) growArray(array, Short.TYPE, additionLen);
        if (additionLen == 1) {
            newArray[newArray.length - 1] = elements[0];
        } else {
            System.arraycopy(elements, 0, newArray, originLen, additionLen);
        }
        return newArray;
    }

    @SafeVarargs
    public static <T> T[] append(final T[] array, final T... elements) {
        final Class<?> clazz;
        if (null != elements) {
            clazz = elements.getClass().getComponentType();
        } else if (null != array) {
            clazz = array.getClass().getComponentType();
        } else {
            throw new IllegalArgumentException("Array and element cannot both be null.");
        }
        final int additionLen = getLength(elements);
        if (additionLen == 0) {
            return clone(array);
        }
        assert elements != null;
        final int originLen = getLength(array);
        @SuppressWarnings("unchecked")
        final T[] newArray = (T[]) growArray(array, clazz, additionLen);
        if (additionLen == 1) {
            newArray[newArray.length - 1] = elements[0];
        } else {
            System.arraycopy(elements, 0, newArray, originLen, additionLen);
        }
        return newArray;
    }

    @SuppressWarnings("all")
    private static Object growArray(final Object array, final Class<?> elementType, int growLen) {
        if (null == array) {
            return Array.newInstance(elementType, growLen);
        }
        final int arrayLength = Array.getLength(array);
        final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + growLen);
        System.arraycopy(array, 0, newArray, 0, arrayLength);
        return newArray;
    }
}
