package ru.nsu.sberlab.gameintegration.tasks;


import lombok.extern.log4j.Log4j2;
import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
import ru.nsu.sberlab.blockchain_interaction.exception.ApplicationLogError;
import ru.nsu.sberlab.gameintegration.data.TransactionInfo;

import java.util.Queue;

import static java.lang.String.format;

/**
 * Задача которая каждые TIME_REQUEST миллисекунд проверяет статус транзакции.
 */
@Log4j2
public class CheckTransactionsTask implements Runnable {

    private static final int TIME_REQUEST = 500;
    private static final long MAX_PAST_TIME = 30000;
    private final Queue<TransactionInfo> queue;
    private final MapInteraction mapInBlockchain;


    /**
     * @param queue очередь с информацией о транзакциях
     */
    public CheckTransactionsTask(Queue<TransactionInfo> queue, MapInteraction mapInBlockchain) {
        this.queue = queue;
        this.mapInBlockchain = mapInBlockchain;
    }


    /**
     * Проверка транзакций.
     */
    public void checkTransactions() {
        while (!queue.isEmpty()) {
            try {
                mapInBlockchain.getResult(queue.peek().getTxHash());
                queue.remove();
            } catch (ApplicationLogError e) {
                if (e.getError().getMessage().equals("Unknown transaction/blockhash") &&
                        queue.peek().getPastTime() > MAX_PAST_TIME
                        || !e.getError().getMessage().equals("Unknown transaction/blockhash")) {
                    log.error(format("wait %s seconds. Didn't get result.", queue.peek().getPastTime() / 1000));
                    //TODO Если транзакция не прошла вернуть прошлый блок
                    queue.remove();
                } else {
                    return;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                queue.remove();
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
