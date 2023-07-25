package ru.nsu.sberlab.gameintegration.tasks;

import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
import ru.nsu.sberlab.blockchain_interaction.exception.ApplicationLogError;
import ru.nsu.sberlab.gameintegration.StaticQueuesWrapper;
import ru.nsu.sberlab.gameintegration.data.TransactionInfo;

import java.util.Queue;

import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * Задача которая каждые TIME_REQUEST миллисекунд проверяет статус транзакции.
 */
public class CheckTransactionsTask implements Runnable {

    private static final int TIME_REQUEST = 500;
    private static final long MAX_PAST_TIME = 30000;
    private final Queue<TransactionInfo> queue;
    private final MapInteraction mapInBlockchain;

    /**
     * @param queue очередь с информацией о транзакциях
     */
    public CheckTransactionsTask(MapInteraction mapInBlockchain, Queue<TransactionInfo> queue) {
        this.queue = queue;
        this.mapInBlockchain = mapInBlockchain;
    }

    /**
     * Проверка транзакций.
     */
    private void checkTransactions() {
        while (!queue.isEmpty()) {
            try {
                mapInBlockchain.getResult(queue.peek().getTxHash());
                queue.remove();
            } catch (ApplicationLogError e) {
                if (e.getError().getMessage().equals("Unknown transaction/blockhash") &&
                        queue.peek().getPastTime() > MAX_PAST_TIME
                        || !e.getError().getMessage().equals("Unknown transaction/blockhash")) {
                    var transactionInfo = queue.remove();
                    StaticQueuesWrapper.sendHistory(transactionInfo.getBlock());
                } else {
                    return;
                }
            } catch (Exception e) {
                var transactionInfo = queue.remove();
                StaticQueuesWrapper.sendHistory(transactionInfo.getBlock());
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(TIME_REQUEST);
                checkTransactions();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
