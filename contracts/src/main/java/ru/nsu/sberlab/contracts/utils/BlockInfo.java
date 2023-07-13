package ru.nsu.sberlab.contracts.utils;

import java.io.Serializable;
import java.util.ArrayList;

import static java.lang.Math.min;
import static java.lang.String.format;

/**
 * Класс BlockInfo нужен для хранения состояния блока.
 */
public class BlockInfo implements Serializable {
    private final int chunkX;
    private final int chunkY;
    private final int x;
    private final int y;
    private final int z;
    private final int blockId;

    private static final int integerSize = 4;

    public static final int BlockInfoByteSize = integerSize * 6;

    /**
     * Метод для десериализации массива объектов
     *
     * @param blockInformationByteRepresentation массив с байтовым представлением нескольких объектов класса
     * @return ArrayList с уже десиалезированными объектами
     */
    public static ArrayList<BlockInfo> getInfoArrayFromByteRepresentation(byte[] blockInformationByteRepresentation)
            throws Exception {
        if (blockInformationByteRepresentation.length % BlockInfoByteSize != 0)
            throw new Exception(format("input array size must be multiple %d", BlockInfoByteSize));

        ArrayList<BlockInfo> resultArray = new ArrayList<>(
                blockInformationByteRepresentation.length / BlockInfoByteSize);

        for (int i = 0; i < blockInformationByteRepresentation.length / BlockInfoByteSize; i++) {
            resultArray.add(new BlockInfo(Utils.byteToInt(blockInformationByteRepresentation, i * BlockInfoByteSize),
                    Utils.byteToInt(blockInformationByteRepresentation, i * BlockInfoByteSize + 4),
                    Utils.byteToInt(blockInformationByteRepresentation, i * BlockInfoByteSize + 8),
                    Utils.byteToInt(blockInformationByteRepresentation, i * BlockInfoByteSize + 12),
                    Utils.byteToInt(blockInformationByteRepresentation, i * BlockInfoByteSize + 16),
                    Utils.byteToInt(blockInformationByteRepresentation, i * BlockInfoByteSize + 20)));
        }
        return resultArray;

    }

    /**
     * @return возвращает байтовое представление полей объекта в порядке: chunkX, chunkY, x, y, z, blockId
     */
    public byte[] getBytesPresentation() {
        byte[] bytes = new byte[BlockInfoByteSize];

        Utils.intToByte(bytes, 0, chunkX);
        Utils.intToByte(bytes, 4, chunkY);
        Utils.intToByte(bytes, 8, x);
        Utils.intToByte(bytes, 12, y);
        Utils.intToByte(bytes, 16, z);
        Utils.intToByte(bytes, 20, blockId);

        return bytes;
    }

    /**
     * @param array массив из 24 байт в котором по порядку лежит байтовое представление: chunkX,
     *              chunkY, x, y, z, blockId
     */
    public BlockInfo(byte[] array) throws Exception {
        if (array.length != BlockInfoByteSize)
            throw new Exception(format("input array size must be %d", BlockInfoByteSize));
        chunkX = Utils.byteToInt(array, 0);
        chunkY = Utils.byteToInt(array, 4);
        x = Utils.byteToInt(array, 8);
        y = Utils.byteToInt(array, 12);
        z = Utils.byteToInt(array, 16);
        blockId = Utils.byteToInt(array, 20);
    }

    @Override
    public String toString() {
        return "BlockInformation{" +
                "chunkX=" + chunkX +
                ", chunkY=" + chunkY +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", blockId=" + blockId +
                '}';
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public BlockInfo(int chunkX, int chunkY, int x, int y, int z, int blockId) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockId = blockId;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    public int getBlockId() {
        return blockId;
    }

    public int getY() {
        return y;
    }

}
