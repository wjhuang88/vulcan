package io.vulcan.utils;

import java.lang.reflect.Array;

public class ArrayUtils {
    public static boolean[] append(final boolean[] array, final boolean element) {
        final boolean[] newArray = (boolean[]) growArray(array, Boolean.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static byte[] append(final byte[] array, final byte element) {
        final byte[] newArray = (byte[]) growArray(array, Byte.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static char[] append(final char[] array, final char element) {
        final char[] newArray = (char[]) growArray(array, Character.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static double[] append(final double[] array, final double element) {
        final double[] newArray = (double[]) growArray(array, Double.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static float[] append(final float[] array, final float element) {
        final float[] newArray = (float[]) growArray(array, Float.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static int[] append(final int[] array, final int element) {
        final int[] newArray = (int[]) growArray(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static long[] append(final long[] array, final long element) {
        final long[] newArray = (long[]) growArray(array, Long.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static short[] append(final short[] array, final short element) {
        final short[] newArray = (short[]) growArray(array, Short.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static <T> T[] append(final T[] array, final T element) {
        final Class<?> clazz;
        if (null != element) {
            clazz = element.getClass();
        } else if (null != array) {
            clazz = array.getClass().getComponentType();
        } else {
            throw new IllegalArgumentException("Array and element cannot both be null.");
        }
        @SuppressWarnings("unchecked")
        final T[] newArray = (T[]) growArray(array, clazz);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    @SuppressWarnings("all")
    private static Object growArray(final Object array, final Class<?> elementType) {
        if (null == array) {
            return Array.newInstance(elementType, 1);
        }
        final int arrayLength = Array.getLength(array);
        final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
        System.arraycopy(array, 0, newArray, 0, arrayLength);
        return newArray;
    }
}
