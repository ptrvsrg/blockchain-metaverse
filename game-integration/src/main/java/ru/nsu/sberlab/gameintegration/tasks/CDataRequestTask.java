package ru.nsu.sberlab.gameintegration.tasks;

import lombok.extern.log4j.Log4j2;
import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
import ru.nsu.sberlab.blockchain_interaction.utils.BlockInfo;
import ru.nsu.sberlab.gameintegration.StaticQueuesWrapper;
import ru.nsu.sberlab.gameintegration.data.Block;
import ru.nsu.sberlab.gameintegration.data.TransactionInfo;
import ru.nsu.sberlab.gameintegration.exception.QueueClosedException;

import java.util.Queue;

/**
 * Класс CDataRequestTask представляет поток для запроса изменений данных из C-кода.
 * Реализует интерфейс Runnable.
 */
@Log4j2
public class CDataRequestTask implements Runnable {
    private final MapInteraction mapInBlockchain;

    private final Queue<TransactionInfo> queue;

    public CDataRequestTask(MapInteraction mapInBlockchain, Queue<TransactionInfo> queue) {
        this.mapInBlockchain = mapInBlockchain;
        this.queue = queue;
    }

    /**
     * Получает изменение блока и отправляет его на запись в блокчейн.
     */
    public void getBlockChange() throws Throwable {


        Block block = StaticQueuesWrapper.getBlockChangeC();
        if (block == null) {
            throw new QueueClosedException();
        }

        var txHash = mapInBlockchain.addChangesNoBlocking(block.getBlockInfoObject());

        queue.add(new TransactionInfo(txHash));

    }


    /**
     * Запускает выполнение задачи.
     * Бесконечно запрашивает изменения данных с помощью нативных методов.
     * В случае прерывания потока выбрасывает исключение InterruptedException.
     */
    @Override
    public void run() {
        while (true) {
            try {
                getBlockChange();
            } catch (QueueClosedException e) {
                return;
            } catch (Throwable e) {
                log.error(e.getMessage());
            }
        }
    }
}
