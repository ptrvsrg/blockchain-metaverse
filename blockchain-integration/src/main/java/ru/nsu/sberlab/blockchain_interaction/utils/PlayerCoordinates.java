package ru.nsu.sberlab.blockchain_interaction.utils;

import java.util.Arrays;
import java.util.Objects;

/**
 * Класс для представления координат игрока в Майнкрафте
 */
public class PlayerCoordinates extends Coordinates {

    public static final int BLOCK_COORDINATES_SIZE = COORDINATES_BYTE_SIZE + 4 * 2;

    private int rx;
    private int ry;

    /**
     * @param serializedObject массив из COORDINATES_BYTE_SIZE + 8 байтов, в котором по порядку идут: rx, ry,
     *                         байтовое представление координат
     * @throws Exception если массив некорректного размера
     */
    public PlayerCoordinates(byte[] serializedObject) throws Exception {
        super(Arrays.copyOfRange(serializedObject, 4 * 2, 4*2 + COORDINATES_BYTE_SIZE));

        if (serializedObject.length != BLOCK_COORDINATES_SIZE) {
            throw new Exception("wrong size of serialized objects");
        }

        rx = Utils.byteToInt(serializedObject, 0);
        ry = Utils.byteToInt(serializedObject, 4);
    }

    public PlayerCoordinates(int rx, int ry, int x, int y, int z) {
        super(x, y, z);
        this.rx = rx;
        this.ry = ry;
    }

    /**
     * @return байтовое представление полей объекта в порядке: байтовое представление rx, ry, Coordinates
     */
    @Override
    public byte[] serialize() {
        byte[] superBytes = super.serialize();
        byte[] serializedObject = new byte[BLOCK_COORDINATES_SIZE];

        Utils.intToByte(serializedObject, 0, rx);
        Utils.intToByte(serializedObject, 4, ry);
        System.arraycopy(superBytes, 0, serializedObject, 8, COORDINATES_BYTE_SIZE);

        return serializedObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PlayerCoordinates that = (PlayerCoordinates) o;
        return rx == that.rx && ry == that.ry;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rx, ry);
    }

    public int getRx() {
        return rx;
    }

    public void setRx(int rx) {
        this.rx = rx;
    }

    public int getRy() {
        return ry;
    }

    public void setRy(int ry) {
        this.ry = ry;
    }
}
