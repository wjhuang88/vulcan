package io.vulcan.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ArrayUtilsTest {

    boolean[] booleanNotEmpty = new boolean[] {true, false, false, true};
    boolean[] booleanEmpty = new boolean[0];

    byte[] byteNotEmpty = new byte[] {1, 2, 3, 4};
    byte[] byteEmpty = new byte[0];

    char[] charNotEmpty = new char[] {'a', 'b', 'c', 'd'};
    char[] charEmpty = new char[0];

    double[] doubleNotEmpty = new double[] {1.1, 2.1, 3.1, 4.1};
    double[] doubleEmpty = new double[0];

    float[] floatNotEmpty = new float[] {1.1F, 2.1F, 3.1F, 4.1F};
    float[] floatEmpty = new float[0];

    int[] intNotEmpty = new int[] {1, 2, 3, 4};
    int[] intEmpty = new int[0];

    long[] longNotEmpty = new long[] {1, 2, 3, 4};
    long[] longEmpty = new long[0];

    short[] shortNotEmpty = new short[] {1, 2, 3, 4};
    short[] shortEmpty = new short[0];

    String[] stringNotEmpty = new String[] {"abc", "def", "ghi", "jkl"};
    String[] stringEmpty = new String[0];

    Integer[] integerNotEmpty = new Integer[] {1, 2, 3, 4};
    Integer[] integerEmpty = new Integer[0];

    int[][] intArrNotEmpty = new int[][] {{1, 2, 3}, {4, 5}};
    int[][] intArrEmpty = new int[0][];

    @Test
    void append() {
        assertArrayEquals(ArrayUtils.append(booleanEmpty, true), new boolean[] {true});
        assertArrayEquals(ArrayUtils.append(booleanNotEmpty, true), new boolean[] {true, false, false, true, true});

        assertArrayEquals(ArrayUtils.append(byteEmpty, (byte) 9), new byte[] {9});
        assertArrayEquals(ArrayUtils.append(byteNotEmpty, (byte) 9), new byte[] {1, 2, 3, 4, 9});

        assertArrayEquals(ArrayUtils.append(charEmpty, 'o'), new char[] {'o'});
        assertArrayEquals(ArrayUtils.append(charNotEmpty, 'o'), new char[] {'a', 'b', 'c', 'd', 'o'});

        assertArrayEquals(ArrayUtils.append(doubleEmpty, 9.9999), new double[] {9.9999});
        assertArrayEquals(ArrayUtils.append(doubleNotEmpty, 9.9999), new double[] {1.1, 2.1, 3.1, 4.1,  9.9999});

        assertArrayEquals(ArrayUtils.append(floatEmpty, 9.9999F), new float[] {9.9999F});
        assertArrayEquals(ArrayUtils.append(floatNotEmpty, 9.9999F), new float[] {1.1F, 2.1F, 3.1F, 4.1F, 9.9999F});

        assertArrayEquals(ArrayUtils.append(intEmpty, 9), new int[] {9});
        assertArrayEquals(ArrayUtils.append(intNotEmpty, 9), new int[] {1, 2, 3, 4, 9});

        assertArrayEquals(ArrayUtils.append(longEmpty, 9), new long[] {9});
        assertArrayEquals(ArrayUtils.append(longNotEmpty, 9), new long[] {1, 2, 3, 4, 9});

        assertArrayEquals(ArrayUtils.append(shortEmpty, (short) 9), new short[] {9});
        assertArrayEquals(ArrayUtils.append(shortNotEmpty, (short) 9), new short[] {1, 2, 3, 4, 9});

        assertArrayEquals(ArrayUtils.append(stringEmpty, "test"), new String[] {"test"});
        assertArrayEquals(ArrayUtils.append(stringNotEmpty, "test"), new String[] {"abc", "def", "ghi", "jkl", "test"});

        assertArrayEquals(ArrayUtils.append(integerEmpty, 9), new Integer[] {9});
        assertArrayEquals(ArrayUtils.append(integerNotEmpty, 9), new Integer[] {1, 2, 3, 4, 9});

        assertArrayEquals(ArrayUtils.append(intArrEmpty, new int[] {9}), new int[][] {{9}});
        assertArrayEquals(ArrayUtils.append(intArrNotEmpty, new int[] {9}), new int[][] {{1, 2, 3}, {4, 5}, {9}});
    }

}