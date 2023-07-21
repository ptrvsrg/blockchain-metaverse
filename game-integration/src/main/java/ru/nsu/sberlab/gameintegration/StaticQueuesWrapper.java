package ru.nsu.sberlab.gameintegration;

import ru.nsu.sberlab.gameintegration.data.Block;

public class StaticQueuesWrapper {
    public static native void sendBlockChangeC(Block block);
    public static native Block getBlockChangeC();
}
