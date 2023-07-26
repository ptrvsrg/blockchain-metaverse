package ru.nsu.sberlab.blockchainintegration.utils;

import java.util.Arrays;
import java.util.Objects;

/**
 * Класс для представления координат блока в Майнкрафте.
 */
public class BlockCoordinates
        extends Coordinates {

    public static final int BLOCK_COORDINATES_SIZE = COORDINATES_BYTE_SIZE + 4 * 2;

    private int chunkX;
    private int chunkY;

    /**
     * @param serializedObject массив из COORDINATES_BYTE_SIZE + 8 байтов, в котором по порядку
     *                         идут: chunkX, chunkY, байтовое представление координат
     * @throws Exception если массив некорректного размера
     */
    public BlockCoordinates(byte[] serializedObject)
            throws Exception {
        super(Arrays.copyOfRange(serializedObject, 4 * 2, 4 * 2 + COORDINATES_BYTE_SIZE));

        if (serializedObject.length != BLOCK_COORDINATES_SIZE) {
            throw new Exception("wrong size of serialized objects");
        }

        chunkX = Utils.byteToInt(serializedObject, 0);
        chunkY = Utils.byteToInt(serializedObject, 4);
    }

    public BlockCoordinates(int chunkX, int chunkY, int x, int y, int z) {
        super(x, y, z);
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    @Override
    public String toString() {
        return "BlockCoordinates{" +
                "chunkX=" + chunkX +
                ", chunkY=" + chunkY +
                ", X=" + getX() +
                ", Y=" + getY() +
                ", Z=" + getZ() +
                '}';
    }

    /**
     * @return байтовое представление полей объекта в порядке: байтовое представление chunkX,
     * chunkY, Coordinates
     */
    @Override
    public byte[] serialize() {
        byte[] superBytes = super.serialize();
        byte[] serializedObject = new byte[BLOCK_COORDINATES_SIZE];

        Utils.intToByte(serializedObject, 0, chunkX);
        Utils.intToByte(serializedObject, 4, chunkY);
        System.arraycopy(superBytes, 0, serializedObject, 8, COORDINATES_BYTE_SIZE);

        return serializedObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BlockCoordinates that = (BlockCoordinates) o;
        return chunkX == that.chunkX && chunkY == that.chunkY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chunkX, chunkY);
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
}
