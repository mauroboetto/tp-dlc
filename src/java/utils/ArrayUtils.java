/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author mauro
 */
public class ArrayUtils {

    public static final int BIG_ENDIAN = 0;
    public static final int LITTLE_ENDIAN = 1;

    public static void byteArrayToIntArray(int[] intArray, byte[] byteArray) 
            throws ArrayCastException {
        byteArrayToIntArray(intArray, byteArray, LITTLE_ENDIAN);
    }
    
    public static void byteArrayToIntArray(int[] intArray, byte[] byteArray, int endianness)
            throws ArrayCastException {
        if ((byteArray.length & 0b11) != 0) {
            throw new ArrayCastException("ByteArray length has to be a multiple of 4");
        }

        int numCount = byteArray.length >> 2; // array length divided by 4

        if (intArray.length < numCount) {
            throw new ArrayCastException("IntArray is too small");
        }

        int i, j;
        switch (endianness) {
            case BIG_ENDIAN:
                for (i = 0, j = 0; i < numCount; i++, j += 4)
                {
                    intArray[i] = (byteArray[j] << 24) | (byteArray[j + 1] << 16) 
                            | (byteArray[j + 2] << 8) | byteArray[j + 3];
                }
                break;
            case LITTLE_ENDIAN:
                for (i = 0, j = 0; i < numCount; i++, j += 4)
                {
                    intArray[i] = byteArray[j] | (byteArray[j + 1] << 8) 
                            | (byteArray[j + 2] << 16) | (byteArray[j + 3] << 24);
                }
        }       
    }
    
    public static void byteArrayToShortArray(short[] shortArray, byte[] byteArray) 
            throws ArrayCastException {
        byteArrayToShortArray(shortArray, byteArray, LITTLE_ENDIAN);
    }    
    
    public static void byteArrayToShortArray(short[] shortArray, byte[] byteArray, int endianness)
            throws ArrayCastException {
        if ((byteArray.length & 0b1) != 0) {
            throw new ArrayCastException("ByteArray length has to be a multiple of 4");
        }

        int numCount = byteArray.length >> 1; // array length divided by 2

        if (shortArray.length < numCount) {
            throw new ArrayCastException("ShortArray is too small");
        }

        int i, j;
        switch (endianness) {
            case BIG_ENDIAN:
                for (i = 0, j = 0; i < numCount; i++, j += 2)
                {
                    shortArray[i] = (short) ((byteArray[j] << 8) | byteArray[j + 1]);
                }
                break;
            case LITTLE_ENDIAN:
                for (i = 0, j = 0; i < numCount; i++, j += 2)
                {
                    shortArray[i] = (short) (byteArray[j] | (byteArray[j + 1] << 8));
                            
                }
        }       
    }
}
