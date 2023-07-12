package blockchain;

import io.neow3j.devpack.Helper;
import io.neow3j.devpack.annotations.Struct;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.lang.Math.min;

@Struct
public class BlockInformation {
    private final int chunkX;
    private final int chunkY;
    private final int x;
    private final int y;
    private final int z;
    private final int blockId;

    private static final int integerSize = 4;

    public static final int BlockInformationByteSize = integerSize * 6;

    private int byteToInt(byte[] bytes, int st) {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res |= ((int) bytes[st + i] >> 8 * i);
        }

        return res;
    }

    private void intToByte(byte[] resultBytes, int st, int number) {

        int oneByte = ~((byte) 0);

        for (int i = 0; i < min(4, resultBytes.length - st); i++) {
            resultBytes[st + i] = (byte) ((number << i * 8) & oneByte);
        }

    }


    public byte[] getBytesPresentation() {
        byte[] bytes = new byte[BlockInformationByteSize];

        intToByte(bytes, 0, chunkX);
        intToByte(bytes, 4, chunkY);
        intToByte(bytes, 8, x);
        intToByte(bytes, 12, y);
        intToByte(bytes, 16, z);
        intToByte(bytes, 20, blockId);

        return bytes;
    }

    public BlockInformation(byte[] array) {

        chunkX = byteToInt(array, 0);
        chunkY = byteToInt(array, 4);
        x = byteToInt(array, 8);
        y = byteToInt(array, 12);
        z = byteToInt(array, 16);
        blockId = byteToInt(array, 20);
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

    public BlockInformation(int chunkX, int chunkY, int x, int y, int z, int blockId) {
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
