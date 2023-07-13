package ru.nsu.sberlab.contracts.utils;

import static java.lang.Math.min;

public class Utils {
    public static int byteToInt(byte[] bytes, int st) {
        int res = 0;
        for (int i = 0; i < min(4, bytes.length - st); i++) {
            res |= ((Byte.toUnsignedInt(bytes[st + i])) << 8 * i);
        }

        return res;
    }

    public static void intToByte(byte[] resultBytes, int st, int number) {

        int oneByte = ~((byte) 0);

        for (int i = 0; i < min(4, resultBytes.length - st); i++) {
            resultBytes[st + i] = (byte) ((number >> i * 8) & oneByte);
        }

    }

}
