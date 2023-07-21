package ru.nsu.sberlab.gameintegration.tasks;


import io.neow3j.protocol.exceptions.RpcResponseErrorException;
import lombok.extern.log4j.Log4j2;
import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
import ru.nsu.sberlab.gameintegration.data.TransactionInfo;

import java.util.Queue;

import static java.lang.String.format;

@Log4j2
public class CheckTransactionsTask implements Runnable {

    private static final long MAX_PAST_TIME = 16000;
    private final Queue<TransactionInfo> queue;
    private final MapInteraction mapInBlockchain;


    public CheckTransactionsTask(Queue<TransactionInfo> queue, MapInteraction mapInBlockchain) {
        this.queue = queue;
        this.mapInBlockchain = mapInBlockchain;
    }


    @Override
    public void run() {

        while (!queue.isEmpty()) {
            try {
                mapInBlockchain.getResult(queue.peek().getTxHash());
            } catch (RpcResponseErrorException e) {
                if (queue.peek().getPastTime() > MAX_PAST_TIME) {
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
}
