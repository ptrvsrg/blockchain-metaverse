package ru.nsu.sberlab.blockchain_interaction.utils;

import static java.lang.String.format;

/**
 * Класс для представления координат в майнкрафте.
 */
public class Coordinates {

    public static final int COORDINATES_BYTE_SIZE = 4 * 5;
    private int chunkX;
    private int chunkY;

    private int x;
    private int y;
    private int z;

    public byte[] serialize() {
        byte[] bytes = new byte[COORDINATES_BYTE_SIZE];

        Utils.intToByte(bytes, 0, chunkX);
        Utils.intToByte(bytes, 4, chunkY);
        Utils.intToByte(bytes, 8, x);
        Utils.intToByte(bytes, 12, y);
        Utils.intToByte(bytes, 16, z);

        return bytes;
    }

    public Coordinates(byte[] serializedObject) throws Exception {
        if (serializedObject.length != COORDINATES_BYTE_SIZE)
            throw new Exception(format("input array size must be %d", COORDINATES_BYTE_SIZE));

        chunkX = Utils.byteToInt(serializedObject, 0);
        chunkY = Utils.byteToInt(serializedObject, 4);
        x = Utils.byteToInt(serializedObject, 8);
        y = Utils.byteToInt(serializedObject, 12);
        z = Utils.byteToInt(serializedObject, 16);
    }

    public Coordinates(int chunkX, int chunkY, int x, int y, int z) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getChunkX() {
        return chunkX;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    public void setChunkY(int chunkY) {
        this.chunkY = chunkY;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
