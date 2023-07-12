package blockchain;

import io.neow3j.devpack.annotations.Struct;

import java.util.ArrayList;

import static java.lang.Math.min;
import static java.lang.String.format;

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

    private static int byteToInt(byte[] bytes, int st) {
        int res = 0;
        for (int i = 0; i < min(4, bytes.length - st); i++) {
            res |= ((Byte.toUnsignedInt(bytes[st + i])) << 8 * i);
        }

        return res;
    }

    private static void intToByte(byte[] resultBytes, int st, int number) {

        int oneByte = ~((byte) 0);

        for (int i = 0; i < min(4, resultBytes.length - st); i++) {
            resultBytes[st + i] = (byte) ((number >> i * 8) & oneByte);
        }

    }


    public static ArrayList<BlockInformation> getInfArrayFromByteRepresentation(byte[] blockInformationByteRepresentation) throws Exception {
        if (blockInformationByteRepresentation.length % BlockInformationByteSize != 0)
            throw new Exception(format("input array size must be multiple %d", BlockInformationByteSize));

        ArrayList<BlockInformation> resultArray = new ArrayList<>(blockInformationByteRepresentation.length / BlockInformationByteSize);

        for (int i = 0; i < blockInformationByteRepresentation.length / BlockInformationByteSize; i++) {
            resultArray.add(new BlockInformation(byteToInt(blockInformationByteRepresentation, i * BlockInformationByteSize),
                    byteToInt(blockInformationByteRepresentation, i * BlockInformationByteSize + 4),
                    byteToInt(blockInformationByteRepresentation, i * BlockInformationByteSize + 8),
                    byteToInt(blockInformationByteRepresentation, i * BlockInformationByteSize + 12),
                    byteToInt(blockInformationByteRepresentation, i * BlockInformationByteSize + 16),
                    byteToInt(blockInformationByteRepresentation, i * BlockInformationByteSize + 20)));
        }
        return resultArray;

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

    public BlockInformation(byte[] array) throws Exception {
        if (array.length != BlockInformationByteSize)
            throw new Exception(format("input array size must be %d", BlockInformationByteSize));
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
