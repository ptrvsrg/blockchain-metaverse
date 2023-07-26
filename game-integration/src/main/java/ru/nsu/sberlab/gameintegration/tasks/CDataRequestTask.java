package ru.nsu.sberlab.gameintegration.tasks;

import java.util.Queue;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import ru.nsu.sberlab.gameintegration.StaticQueuesWrapper;
import ru.nsu.sberlab.gameintegration.data.Block;
import ru.nsu.sberlab.gameintegration.exception.QueueClosedException;

/**
 * Класс CDataRequestTask представляет поток для запроса изменений данных из C-кода. Реализует
 * интерфейс Runnable.
 */
@Log4j2
public class CDataRequestTask
    implements Runnable {

    private final Queue<Block> queueChanges;

    public CDataRequestTask(Queue<Block> queueChanges) {
        this.queueChanges = queueChanges;
    }

    /**
     * Получает изменение блока и отправляет их в очередь.
     */
    public void getBlockChange()
        throws Throwable {
        Block block = StaticQueuesWrapper.getBlockChangeC();
        if (block == null) {
            throw new QueueClosedException();
        }

        queueChanges.add(block);
    }

    /**
     * Запускает выполнение задачи. Бесконечно запрашивает изменения данных с помощью нативных
     * методов. В случае прерывания потока выбрасывает исключение InterruptedException.
     */
    @Override
    public void run() {
        while (true) {
            try {
                getBlockChange();
            } catch (QueueClosedException e) {
                return;
            } catch (Throwable e) {
                log.catching(Level.ERROR, e);
            }
        }
    }
}
