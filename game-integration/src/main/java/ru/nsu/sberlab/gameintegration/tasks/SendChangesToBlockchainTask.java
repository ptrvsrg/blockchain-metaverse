package ru.nsu.sberlab.gameintegration.tasks;

import io.neow3j.types.Hash256;
import java.util.Arrays;
import java.util.Queue;
import ru.nsu.sberlab.blockchainintegration.MapInteraction;
import ru.nsu.sberlab.blockchainintegration.utils.BlockInfo;
import ru.nsu.sberlab.gameintegration.StaticQueuesWrapper;
import ru.nsu.sberlab.gameintegration.data.Block;
import ru.nsu.sberlab.gameintegration.data.TransactionInfo;

/**
 * Посылает изменения из очереди в блокчейн.
 */
public class SendChangesToBlockchainTask
    implements Runnable {

    private static final int TIME_REQUEST = 10000;
    private static final int MAX_BUFFER_SIZE = 100;
    private final MapInteraction mapInBlockchain;
    private final Queue<TransactionInfo> queueTransactions;
    private final Queue<Block> queueChanges;


    public SendChangesToBlockchainTask(MapInteraction mapInBlockchain,
                                       Queue<TransactionInfo> queueTransactions,
                                       Queue<Block> queueChanges) {
        this.mapInBlockchain = mapInBlockchain;
        this.queueTransactions = queueTransactions;
        this.queueChanges = queueChanges;
    }

    public void sendTransaction() {
        int i;
        var blockInfoArray = new BlockInfo[MAX_BUFFER_SIZE];
        var blockArray = new Block[MAX_BUFFER_SIZE];

        for (i = 0; i < MAX_BUFFER_SIZE && !queueChanges.isEmpty(); i++) {
            var block = queueChanges.remove();
            blockArray[i] = block;
            blockInfoArray[i] = block.getBlockInfoObject();
        }
        if (i == 0) {
            return;
        }
        blockInfoArray = Arrays.copyOfRange(blockInfoArray, 0, i);
        blockArray = Arrays.copyOfRange(blockArray, 0, i);

        Hash256 txHash;

        try {
            txHash = mapInBlockchain.addChangesNoBlocking(blockInfoArray);
            queueTransactions.add(new TransactionInfo(txHash, blockArray));
        } catch (Throwable e) {
            StaticQueuesWrapper.sendHistory(blockArray);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(TIME_REQUEST);
                sendTransaction();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
