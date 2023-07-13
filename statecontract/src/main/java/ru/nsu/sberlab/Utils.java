package ru.nsu.sberlab;

import static java.lang.Math.min;

/**
 * Класс Utils предоставляет утилитарные методы для преобразования данных.
 */
public class Utils {

    /**
     * Преобразует массив байтов в целое число.
     *
     * @param bytes массив байтов
     * @param st    начальная позиция в массиве
     * @return преобразованное целое число
     */
    public static int byteToInt(byte[] bytes, int st) {
        int res = 0;
        for (int i = 0; i < Math.min(4, bytes.length - st); i++) {
            res |= ((Byte.toUnsignedInt(bytes[st + i])) << 8 * i);
        }
        return res;
    }

    /**
     * Преобразует целое число в массив байтов.
     *
     * @param resultBytes массив байтов для записи результата
     * @param st          начальная позиция в массиве
     * @param number      целое число для преобразования
     */
    public static void intToByte(byte[] resultBytes, int st, int number) {
        int oneByte = ~((byte) 0);
        for (int i = 0; i < Math.min(4, resultBytes.length - st); i++) {
            resultBytes[st + i] = (byte) ((number >> i * 8) & oneByte);
        }
    }

    /**
     * Преобразует массив целых чисел в массив байтов.
     *
     * @param intArray массив целых чисел
     * @return массив байтов
     */
    public static byte[] intArrayToByteArray(int[] intArray) {
        byte[] byteArray = new byte[intArray.length * 4];
        for (int i = 0; i < intArray.length; i++) {
            intToByte(byteArray, i * 4, intArray[i]);
        }
        return byteArray;
    }

    /**
     * Преобразует массив байтов в массив целых чисел.
     *
     * @param byteArray массив байтов
     * @return массив целых чисел
     */
    public static int[] ByteArrayToIntArray(byte[] byteArray) {
        int[] intArray = new int[byteArray.length / 4];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = byteToInt(byteArray, 4 * i);
        }
        return intArray;
    }
}

