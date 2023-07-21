package ru.nsu.sberlab.blockchain_interaction.utils;

import java.util.Objects;

import static java.lang.Float.floatToIntBits;
import static java.lang.Float.intBitsToFloat;

/**
 * Класс для представления координат игрока в Майнкрафте
 */
public class PlayerCoordinates {

    public static final int BLOCK_COORDINATES_SIZE = 4 * 5;

    private float x;
    private float y;
    private float z;
    private float rx;
    private float ry;

    /**
     * @param serializedObject массив из COORDINATES_BYTE_SIZE + 8 байтов, в котором по порядку идут: rx, ry, x, y, z
     * @throws Exception если массив некорректного размера
     */
    public PlayerCoordinates(byte[] serializedObject) throws Exception {

        rx = intBitsToFloat(Utils.byteToInt(serializedObject, 0));
        ry = intBitsToFloat(Utils.byteToInt(serializedObject, 4));
        x = intBitsToFloat(Utils.byteToInt(serializedObject, 12));
        y = intBitsToFloat(Utils.byteToInt(serializedObject, 16));
        z = intBitsToFloat(Utils.byteToInt(serializedObject, 20));

    }

    public PlayerCoordinates(float rx, float ry, float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rx = rx;
        this.ry = ry;
    }

    /**
     * @return байтовое представление полей объекта в порядке: байтовое представление rx, ry, x, y, z
     */
    public byte[] serialize() {
        byte[] serializedObject = new byte[BLOCK_COORDINATES_SIZE];

        Utils.intToByte(serializedObject, 0, floatToIntBits(rx));
        Utils.intToByte(serializedObject, 4, floatToIntBits(ry));
        Utils.intToByte(serializedObject, 8, floatToIntBits(x));
        Utils.intToByte(serializedObject, 12, floatToIntBits(y));
        Utils.intToByte(serializedObject, 16, floatToIntBits(z));

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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getRx() {
        return rx;
    }

    public void setRx(float rx) {
        this.rx = rx;
    }

    public float getRy() {
        return ry;
    }

    public void setRy(float ry) {
        this.ry = ry;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rx, ry, x, y, z);
    }


}
