package ru.nsu.sberlab.blockchainintegration.utils;

import static java.lang.String.format;

import java.util.Objects;

/**
 * Класс для представления координат.
 */
public class Coordinates {

    public static final int COORDINATES_BYTE_SIZE = 4 * 3;
    private int x;
    private int y;
    private int z;

    /**
     * @param serializedObject массив из 12 байт, в котором по порядку идут: x, y, z
     * @throws Exception если массив некорректного размера
     */
    public Coordinates(byte[] serializedObject)
        throws Exception {
        if (serializedObject.length != COORDINATES_BYTE_SIZE) {
            throw new Exception(format("input array size must be %d", COORDINATES_BYTE_SIZE));
        }

        x = Utils.byteToInt(serializedObject, 0);
        y = Utils.byteToInt(serializedObject, 4);
        z = Utils.byteToInt(serializedObject, 8);
    }

    /**
     * Конструктор класса Coordinates. Создает новый объект координат с заданными значениями x, y и
     * z.
     *
     * @param x Значение координаты x.
     * @param y Значение координаты y.
     * @param z Значение координаты z.
     */
    public Coordinates(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * @return байтовое представление в порядке: x, y, z
     */
    public byte[] serialize() {
        byte[] bytes = new byte[COORDINATES_BYTE_SIZE];

        Utils.intToByte(bytes, 0, x);
        Utils.intToByte(bytes, 4, y);
        Utils.intToByte(bytes, 8, z);

        return bytes;
    }

    @Override
    public String toString() {
        return "Coordinates{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
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
