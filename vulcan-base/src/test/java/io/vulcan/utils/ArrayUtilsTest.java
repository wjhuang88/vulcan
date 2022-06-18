package io.vulcan.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ArrayUtilsTest {

    boolean[] booleanNotEmpty = ArrayUtils.makeUnboxing(true, false, false, true);
    boolean[] booleanEmpty = new boolean[0];

    byte[] byteNotEmpty = ArrayUtils.makeUnboxing((byte)1, (byte)2, (byte)3, (byte)4);
    byte[] byteEmpty = new byte[0];

    char[] charNotEmpty = ArrayUtils.makeUnboxing('a', 'b', 'c', 'd');
    char[] charEmpty = new char[0];

    double[] doubleNotEmpty = ArrayUtils.makeUnboxing(1.1, 2.1, 3.1, 4.1);
    double[] doubleEmpty = new double[0];

    float[] floatNotEmpty = ArrayUtils.makeUnboxing(1.1F, 2.1F, 3.1F, 4.1F);
    float[] floatEmpty = new float[0];

    int[] intNotEmpty = ArrayUtils.makeUnboxing(1, 2, 3, 4);
    int[] intEmpty = new int[0];

    long[] longNotEmpty = ArrayUtils.makeUnboxing(1L, 2L, 3L, 4L);
    long[] longEmpty = new long[0];

    short[] shortNotEmpty = ArrayUtils.makeUnboxing((short)1, (short)2, (short)3, (short)4);
    short[] shortEmpty = new short[0];

    String[] stringNotEmpty = ArrayUtils.make("abc", "def", "ghi", "jkl");
    String[] stringEmpty = new String[0];

    Integer[] integerNotEmpty = ArrayUtils.make(1, 2, 3, 4);
    Integer[] integerEmpty = new Integer[0];

    int[][] intArrNotEmpty = ArrayUtils.make(ArrayUtils.makeUnboxing(1, 2, 3), ArrayUtils.makeUnboxing(4, 5));
    int[][] intArrEmpty = new int[0][];

    @Test
    void appendTest() {
        assertArrayEquals(ArrayUtils.append((boolean[]) null, true), new boolean[] {true});
        assertArrayEquals(ArrayUtils.append(booleanEmpty, true), new boolean[] {true});
        assertArrayEquals(ArrayUtils.append(booleanNotEmpty, true), new boolean[] {true, false, false, true, true});
        assertArrayEquals(ArrayUtils.append(booleanNotEmpty, true, false, false), new boolean[] {true, false, false, true, true, false, false});

        assertArrayEquals(ArrayUtils.append((byte[]) null, (byte) 9), new byte[] {9});
        assertArrayEquals(ArrayUtils.append(byteEmpty, (byte) 9), new byte[] {9});
        assertArrayEquals(ArrayUtils.append(byteNotEmpty, (byte) 9), new byte[] {1, 2, 3, 4, 9});
        assertArrayEquals(ArrayUtils.append(byteNotEmpty, (byte) 9, (byte) 10), new byte[] {1, 2, 3, 4, 9, 10});

        assertArrayEquals(ArrayUtils.append((char[]) null, 'o'), new char[] {'o'});
        assertArrayEquals(ArrayUtils.append(charEmpty, 'o'), new char[] {'o'});
        assertArrayEquals(ArrayUtils.append(charNotEmpty, 'o'), new char[] {'a', 'b', 'c', 'd', 'o'});
        assertArrayEquals(ArrayUtils.append(charNotEmpty, 'o', '0'), new char[] {'a', 'b', 'c', 'd', 'o', '0'});

        assertArrayEquals(ArrayUtils.append((double[]) null, 9.9999), new double[] {9.9999});
        assertArrayEquals(ArrayUtils.append(doubleEmpty, 9.9999), new double[] {9.9999});
        assertArrayEquals(ArrayUtils.append(doubleNotEmpty, 9.9999), new double[] {1.1, 2.1, 3.1, 4.1, 9.9999});
        assertArrayEquals(ArrayUtils.append(doubleNotEmpty, 9.9999, 10.0), new double[] {1.1, 2.1, 3.1, 4.1, 9.9999, 10.0});

        assertArrayEquals(ArrayUtils.append((float[]) null, 9.9999F), new float[] {9.9999F});
        assertArrayEquals(ArrayUtils.append(floatEmpty, 9.9999F), new float[] {9.9999F});
        assertArrayEquals(ArrayUtils.append(floatNotEmpty, 9.9999F), new float[] {1.1F, 2.1F, 3.1F, 4.1F, 9.9999F});
        assertArrayEquals(ArrayUtils.append(floatNotEmpty, 9.9999F, 10F, 0.001F), new float[] {1.1F, 2.1F, 3.1F, 4.1F, 9.9999F, 10F, 0.001F});

        assertArrayEquals(ArrayUtils.append((int[]) null, 9), new int[] {9});
        assertArrayEquals(ArrayUtils.append(intEmpty, 9), new int[] {9});
        assertArrayEquals(ArrayUtils.append(intNotEmpty, 9), new int[] {1, 2, 3, 4, 9});
        assertArrayEquals(ArrayUtils.append(intNotEmpty, 9, 10), new int[] {1, 2, 3, 4, 9, 10});

        assertArrayEquals(ArrayUtils.append((long[]) null, 9), new long[] {9});
        assertArrayEquals(ArrayUtils.append(longEmpty, 9), new long[] {9});
        assertArrayEquals(ArrayUtils.append(longNotEmpty, 9), new long[] {1, 2, 3, 4, 9});
        assertArrayEquals(ArrayUtils.append(longNotEmpty, 9, 10), new long[] {1, 2, 3, 4, 9, 10});

        assertArrayEquals(ArrayUtils.append((short[]) null, (short) 9), new short[] {9});
        assertArrayEquals(ArrayUtils.append(shortEmpty, (short) 9), new short[] {9});
        assertArrayEquals(ArrayUtils.append(shortNotEmpty, (short) 9), new short[] {1, 2, 3, 4, 9});
        assertArrayEquals(ArrayUtils.append(shortNotEmpty, (short) 9, (short) 10), new short[] {1, 2, 3, 4, 9, 10});

        assertArrayEquals(ArrayUtils.append(null, "test"), new String[] {"test"});
        assertArrayEquals(ArrayUtils.append(stringEmpty, "test"), new String[] {"test"});
        assertArrayEquals(ArrayUtils.append(stringNotEmpty, "test"), new String[] {"abc", "def", "ghi", "jkl", "test"});
        assertArrayEquals(ArrayUtils.append(stringNotEmpty, "test", "test2"), new String[] {"abc", "def", "ghi", "jkl", "test", "test2"});

        assertArrayEquals(ArrayUtils.append((Integer[]) null, 9), new Integer[] {9});
        assertArrayEquals(ArrayUtils.append(integerEmpty, 9), new Integer[] {9});
        assertArrayEquals(ArrayUtils.append(integerNotEmpty, 9), new Integer[] {1, 2, 3, 4, 9});
        assertArrayEquals(ArrayUtils.append(integerNotEmpty, 9, 10, 11), new Integer[] {1, 2, 3, 4, 9, 10, 11});

        assertArrayEquals(ArrayUtils.append((int[][]) null, new int[] {9}), new int[][] {{9}});
        assertArrayEquals(ArrayUtils.append(intArrEmpty, new int[] {9}), new int[][] {{9}});
        assertArrayEquals(ArrayUtils.append(intArrNotEmpty, new int[] {9}), new int[][] {{1, 2, 3}, {4, 5}, {9}});
        assertArrayEquals(ArrayUtils.append(intArrNotEmpty, new int[] {9}, new int[] {9}), new int[][] {{1, 2, 3}, {4, 5}, {9}, {9}});
    }

    @Test
    void insertTest() {
        assertNull(ArrayUtils.insert((boolean[]) null, true));
        assertArrayEquals(ArrayUtils.insert(booleanEmpty, true), new boolean[] {true});
        assertArrayEquals(ArrayUtils.insert(booleanNotEmpty, true), new boolean[] {true, true, false, false, true});
        assertArrayEquals(ArrayUtils.insert(2, booleanNotEmpty, true), new boolean[] {true, false, true, false, true});
        assertArrayEquals(ArrayUtils.insert(booleanNotEmpty, true, false, false), new boolean[] {true, false, false, true, false, false, true});

        assertNull(ArrayUtils.insert((byte[]) null, (byte) 9));
        assertArrayEquals(ArrayUtils.insert(byteEmpty, (byte) 9), new byte[] {9});
        assertArrayEquals(ArrayUtils.insert(byteNotEmpty, (byte) 9), new byte[] {9, 1, 2, 3, 4});
        assertArrayEquals(ArrayUtils.insert(2, byteNotEmpty, (byte) 9), new byte[] {1, 2, 9, 3, 4});
        assertArrayEquals(ArrayUtils.insert(byteNotEmpty, (byte) 9, (byte) 10), new byte[] {9, 10, 1, 2, 3, 4});

        assertNull(ArrayUtils.insert((char[]) null, 'o'));
        assertArrayEquals(ArrayUtils.insert(charEmpty, 'o'), new char[] {'o'});
        assertArrayEquals(ArrayUtils.insert(charNotEmpty, 'o'), new char[] {'o', 'a', 'b', 'c', 'd'});
        assertArrayEquals(ArrayUtils.insert(2, charNotEmpty, 'o'), new char[] {'a', 'b', 'o', 'c', 'd'});
        assertArrayEquals(ArrayUtils.insert(charNotEmpty, 'o', '0'), new char[] {'o', '0', 'a', 'b', 'c', 'd'});

        assertNull(ArrayUtils.insert((double[]) null, 9.9999));
        assertArrayEquals(ArrayUtils.insert(doubleEmpty, 9.9999), new double[] {9.9999});
        assertArrayEquals(ArrayUtils.insert(doubleNotEmpty, 9.9999), new double[] {9.9999, 1.1, 2.1, 3.1, 4.1});
        assertArrayEquals(ArrayUtils.insert(2, doubleNotEmpty, 9.9999), new double[] {1.1, 2.1, 9.9999, 3.1, 4.1});
        assertArrayEquals(ArrayUtils.insert(doubleNotEmpty, 9.9999, 10.0), new double[] {9.9999, 10.0, 1.1, 2.1, 3.1, 4.1});

        assertNull(ArrayUtils.insert((float[]) null, 9.9999F));
        assertArrayEquals(ArrayUtils.insert(floatEmpty, 9.9999F), new float[] {9.9999F});
        assertArrayEquals(ArrayUtils.insert(floatNotEmpty, 9.9999F), new float[] {9.9999F, 1.1F, 2.1F, 3.1F, 4.1F});
        assertArrayEquals(ArrayUtils.insert(2, floatNotEmpty, 9.9999F), new float[] {1.1F, 2.1F, 9.9999F, 3.1F, 4.1F});
        assertArrayEquals(ArrayUtils.insert(floatNotEmpty, 9.9999F, 10F, 0.001F), new float[] {9.9999F, 10F, 0.001F, 1.1F, 2.1F, 3.1F, 4.1F});

        assertNull(ArrayUtils.insert((int[]) null, 9));
        assertArrayEquals(ArrayUtils.insert(intEmpty, 9), new int[] {9});
        assertArrayEquals(ArrayUtils.insert(intNotEmpty, 9), new int[] {9, 1, 2, 3, 4});
        assertArrayEquals(ArrayUtils.insert(2, intNotEmpty, 9), new int[] {1, 2, 9, 3, 4});
        assertArrayEquals(ArrayUtils.insert(intNotEmpty, 9, 10), new int[] {9, 10, 1, 2, 3, 4});

        assertNull(ArrayUtils.insert((long[]) null, 9));
        assertArrayEquals(ArrayUtils.insert(longEmpty, 9), new long[] {9});
        assertArrayEquals(ArrayUtils.insert(longNotEmpty, 9), new long[] {9, 1, 2, 3, 4});
        assertArrayEquals(ArrayUtils.insert(2, longNotEmpty, 9), new long[] {1, 2, 9, 3, 4});
        assertArrayEquals(ArrayUtils.insert(longNotEmpty, 9, 10), new long[] {9, 10, 1, 2, 3, 4});

        assertNull(ArrayUtils.insert((short[]) null, (short) 9));
        assertArrayEquals(ArrayUtils.insert(shortEmpty, (short) 9), new short[] {9});
        assertArrayEquals(ArrayUtils.insert(shortNotEmpty, (short) 9), new short[] {9, 1, 2, 3, 4});
        assertArrayEquals(ArrayUtils.insert(2, shortNotEmpty, (short) 9), new short[] {1, 2, 9, 3, 4});
        assertArrayEquals(ArrayUtils.insert(shortNotEmpty, (short) 9, (short) 10), new short[] {9, 10, 1, 2, 3, 4});

        assertNull(ArrayUtils.insert(null, "test"));
        assertArrayEquals(ArrayUtils.insert(stringEmpty, "test"), new String[] {"test"});
        assertArrayEquals(ArrayUtils.insert(stringNotEmpty, "test"), new String[] {"test", "abc", "def", "ghi", "jkl"});
        assertArrayEquals(ArrayUtils.insert(2, stringNotEmpty, "test"), new String[] {"abc", "def", "test", "ghi", "jkl"});
        assertArrayEquals(ArrayUtils.insert(stringNotEmpty, "test", "test2"), new String[] {"test", "test2", "abc", "def", "ghi", "jkl"});

        assertNull(ArrayUtils.insert((Integer[]) null, 9));
        assertArrayEquals(ArrayUtils.insert(integerEmpty, 9), new Integer[] {9});
        assertArrayEquals(ArrayUtils.insert(integerNotEmpty, 9), new Integer[] {9, 1, 2, 3, 4});
        assertArrayEquals(ArrayUtils.insert(2, integerNotEmpty, 9), new Integer[] {1, 2, 9, 3, 4});
        assertArrayEquals(ArrayUtils.insert(integerNotEmpty, 9, 10, 11), new Integer[] {9, 10, 11, 1, 2, 3, 4});

        assertNull(ArrayUtils.insert((int[][]) null, new int[] {9}));
        assertArrayEquals(ArrayUtils.insert(intArrEmpty, new int[] {9}), new int[][] {{9}});
        assertArrayEquals(ArrayUtils.insert(intArrNotEmpty, new int[] {9}), new int[][] {{9}, {1, 2, 3}, {4, 5}});
        assertArrayEquals(ArrayUtils.insert(2, intArrNotEmpty, new int[] {9}), new int[][] {{1, 2, 3}, {4, 5}, {9}});
        assertArrayEquals(ArrayUtils.insert(intArrNotEmpty, new int[] {9}, new int[] {9}), new int[][] {{9}, {9}, {1, 2, 3}, {4, 5}});
    }

}