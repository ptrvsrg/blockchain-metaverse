package ru.nsu.sberlab.gameintegration;

import ru.nsu.sberlab.gameintegration.data.Block;

public class StaticQueuesWrapper {

    /**
     * Посылает в очередь си блоки предшествующие блокам из blockArray.
     *
     * @param blockArray массив блоков
     */
    public static void sendHistory(Block... blockArray) {
        for (int j = blockArray.length - 1; j >= 0; j--) {
            StaticQueuesWrapper.sendBlockChangeC(blockArray[j].getHistoryBlock());
        }
    }

    public static native void sendBlockChangeC(Block block);

    public static native Block getBlockChangeC();

    public static native void init();
}
