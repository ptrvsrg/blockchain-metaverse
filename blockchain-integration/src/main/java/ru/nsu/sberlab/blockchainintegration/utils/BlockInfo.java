package ru.nsu.sberlab.blockchainintegration.utils;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Класс BlockInfo нужен для хранения состояния блока.
 */
public class BlockInfo {

    private static final int integerSize = 4;
    public static final int BlockInfoByteSize =
        integerSize + BlockCoordinates.BLOCK_COORDINATES_SIZE;
    private final int blockId;
    private BlockCoordinates coordinates;

    /**
     * @param serializedObject массив из 24 байт в котором по порядку лежит байтовое представление:
     *                         coordinates, blockId
     */
    public BlockInfo(byte[] serializedObject)
        throws Exception {
        if (serializedObject.length != BlockInfoByteSize) {
            throw new Exception(
                format("input serializedObject size must be %d", BlockInfoByteSize));
        }

        coordinates = new BlockCoordinates(Arrays.copyOfRange(serializedObject, 0, 20));
        blockId = Utils.byteToInt(serializedObject, 20);
    }

    public BlockInfo(BlockCoordinates coordinates, int blockId) {
        this.coordinates = coordinates;
        this.blockId = blockId;
    }

    public BlockInfo(int chunkX, int chunkY, int x, int y, int z, int blockId) {
        coordinates = new BlockCoordinates(chunkX, chunkY, x, y, z);
        this.blockId = blockId;
    }

    /**
     * Метод для десериализации массива объектов
     *
     * @param blockInformationByteRepresentation массив с байтовым представлением нескольких
     *                                           объектов класса
     * @return ArrayList с уже десиалезированными объектами
     */
    public static ArrayList<BlockInfo> getInfoArrayFromByteRepresentation(
        byte[] blockInformationByteRepresentation)
        throws Exception {
        if (blockInformationByteRepresentation.length % BlockInfoByteSize != 0) {
            throw new Exception(format("input array size must be multiple %d", BlockInfoByteSize));
        }

        ArrayList<BlockInfo> resultArray = new ArrayList<>(
            blockInformationByteRepresentation.length / BlockInfoByteSize);

        for (int i = 0; i < blockInformationByteRepresentation.length / BlockInfoByteSize; i++) {
            resultArray.add(new BlockInfo(new BlockCoordinates(
                Arrays.copyOfRange(blockInformationByteRepresentation, i * BlockInfoByteSize,
                                   i * BlockInfoByteSize + 20)),
                                          Utils.byteToInt(blockInformationByteRepresentation,
                                                          i * BlockInfoByteSize + 20)));
        }
        return resultArray;
    }

    /**
     * Преобразует объект в массив байтов (сериализация). Массив байтов содержит информацию о
     * координатах блока и идентификаторе блока.
     *
     * @return Массив байтов, представляющий сериализованный объект.
     */
    public byte[] serialize() {
        byte[] bytes = new byte[BlockInfoByteSize];

        System.arraycopy(coordinates.serialize(), 0, bytes, 0,
                         BlockCoordinates.BLOCK_COORDINATES_SIZE);
        Utils.intToByte(bytes, 20, blockId);

        return bytes;
    }

    @Override
    public String toString() {
        return "BlockInfo{" + "coordinates=" + coordinates + ", blockId=" + blockId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockInfo blockInfo = (BlockInfo) o;
        return blockId == blockInfo.blockId && Objects.equals(coordinates, blockInfo.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, blockId);
    }

    public BlockCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(BlockCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public int getBlockId() {
        return blockId;
    }
}
