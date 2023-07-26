package ru.nsu.sberlab.gameintegration.data;

import lombok.AllArgsConstructor;
import lombok.Value;
import ru.nsu.sberlab.blockchainintegration.utils.BlockInfo;

@Value
@AllArgsConstructor
public class Block {

    private static final int EMPTY_FIELD = -1;
    int p;
    int q;
    int x;
    int y;
    int z;
    int w;
    int old_w;

    public Block(BlockInfo blockInfo) {
        p = blockInfo.getCoordinates()
                     .getChunkX();
        q = blockInfo.getCoordinates()
                     .getChunkY();
        x = blockInfo.getCoordinates()
                     .getX();
        y = blockInfo.getCoordinates()
                     .getY();
        z = blockInfo.getCoordinates()
                     .getZ();
        w = blockInfo.getBlockId();
        old_w = 0;
    }

    @Override
    public String toString() {
        return "Block{" + "p=" + p + ", q=" + q + ", x=" + x + ", y=" + y + ", z=" + z + ", w=" +
               w + ", old_w=" + old_w + '}';
    }

    public Block getHistoryBlock() {
        return new Block(p, q, x, y, z, old_w, EMPTY_FIELD);
    }

    public BlockInfo getBlockInfoObject() {
        return new BlockInfo(p, q, x, y, z, w);
    }
}
