package io.vulcan.utils;

import java.lang.reflect.Array;

public final class ArrayUtils {

    private ArrayUtils() {}

    public static int getLength(final Object array) {
        if (null == array) {
            return 0;
        }
        return Array.getLength(array);
    }

    public static boolean isEmpty(final boolean[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(final byte[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(final char[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(final double[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(final float[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(final int[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(final long[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(final short[] array) {
        return getLength(array) == 0;
    }

    public static boolean isEmpty(final Object[] array) {
        return getLength(array) == 0;
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

    public static boolean[] insert(final boolean[] array, final boolean... values) {
        return insert(0, array, values);
    }

    public static boolean[] insert(final int index, final boolean[] array, final boolean... values) {
        if (array == null) {
            return null;
        }
        if (isEmpty(values)) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final boolean[] result = new boolean[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    public static byte[] insert(final byte[] array, final byte... values) {
        return insert(0, array, values);
    }

    public static byte[] insert(final int index, final byte[] array, final byte... values) {
        if (array == null) {
            return null;
        }
        if (isEmpty(values)) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final byte[] result = new byte[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    public static char[] insert(final char[] array, final char... values) {
        return insert(0, array, values);
    }

    public static char[] insert(final int index, final char[] array, final char... values) {
        if (array == null) {
            return null;
        }
        if (isEmpty(values)) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final char[] result = new char[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    public static double[] insert(final double[] array, final double... values) {
        return insert(0, array, values);
    }

    public static double[] insert(final int index, final double[] array, final double... values) {
        if (array == null) {
            return null;
        }
        if (isEmpty(values)) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final double[] result = new double[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    public static float[] insert(final float[] array, final float... values) {
        return insert(0, array, values);
    }

    public static float[] insert(final int index, final float[] array, final float... values) {
        if (array == null) {
            return null;
        }
        if (isEmpty(values)) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final float[] result = new float[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    public static int[] insert(final int[] array, final int... values) {
        return insert(0, array, values);
    }

    public static int[] insert(final int index, final int[] array, final int... values) {
        if (array == null) {
            return null;
        }
        if (isEmpty(values)) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final int[] result = new int[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    public static long[] insert(final long[] array, final long... values) {
        return insert(0, array, values);
    }

    public static long[] insert(final int index, final long[] array, final long... values) {
        if (array == null) {
            return null;
        }
        if (isEmpty(values)) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final long[] result = new long[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    public static short[] insert(final short[] array, final short... values) {
        return insert(0, array, values);
    }

    public static short[] insert(final int index, final short[] array, final short... values) {
        if (array == null) {
            return null;
        }
        if (isEmpty(values)) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final short[] result = new short[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    @SafeVarargs
    public static <T> T[] insert(final T[] array, final T... values) {
        return insert(0, array, values);
    }

    @SafeVarargs
    public static <T> T[] insert(final int index, final T[] array, final T... values) {

        if (array == null) {
            return null;
        }
        if (isEmpty(values)) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final Class<?> type = array.getClass().getComponentType();
        @SuppressWarnings("unchecked")
        final T[] result = (T[]) Array.newInstance(type, array.length + values.length);

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }
}
