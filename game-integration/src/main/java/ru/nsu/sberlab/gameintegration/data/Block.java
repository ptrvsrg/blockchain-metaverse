package ru.nsu.sberlab.gameintegration.data;

import lombok.Value;
import ru.nsu.sberlab.blockchain_interaction.utils.BlockInfo;

@Value
public class Block {
    int p;
    int q;
    int x;
    int y;
    int z;
    int w;
    int old_w;

    @Override
    public String toString() {
        return "Block{" +
                "p=" + p +
                ", q=" + q +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                ", old_w=" + old_w +
                '}';
    }

    public Block(int p, int q, int x, int y, int z, int w, int old_w) {
        this.p = p;
        this.q = q;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.old_w = old_w;
    }

    public Block(BlockInfo blockInfo) {
        p = blockInfo.getCoordinates().getChunkX();
        q = blockInfo.getCoordinates().getChunkY();
        x = blockInfo.getCoordinates().getX();
        y = blockInfo.getCoordinates().getY();
        z = blockInfo.getCoordinates().getZ();
        w = blockInfo.getBlockId();
        old_w = 0;
    }

    public BlockInfo getBlockInfoObject() {
        return new BlockInfo(p, q, x, y, z, w);
    }
}
