package ru.nsu.sberlab.gameintegration.tasks;

import lombok.extern.log4j.Log4j2;
import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
import ru.nsu.sberlab.blockchain_interaction.utils.BlockInfo;
import ru.nsu.sberlab.gameintegration.StaticQueuesWrapper;
import ru.nsu.sberlab.gameintegration.data.Block;
import ru.nsu.sberlab.gameintegration.exception.QueueClosedException;

import java.util.Arrays;

/**
 * Класс CDataRequestTask представляет поток для запроса изменений данных из C-кода.
 * Реализует интерфейс Runnable.
 */
@Log4j2
public class CDataRequestTask implements Runnable {
    /**
     * Время между запросами на изменение данных (в миллисекундах).
     */
    private static final int TIME_REQUEST = 1500;

    private static final int MAX_BUFFER_SIZE = 20;
    //    private int positionToAddNewInfo = 0;
    private final MapInteraction mapInBlockchain;
    private final BlockInfo[] infoBuffer = new BlockInfo[MAX_BUFFER_SIZE];
    private int positionToAddInfo = 0;

    public CDataRequestTask(MapInteraction mapInBlockchain) {
        this.mapInBlockchain = mapInBlockchain;
    }

    /**
     * Получает изменение блока и отправляет его на запись в блокчейн.
     */
    public void getBlockChange() throws Throwable {
        if (positionToAddInfo == MAX_BUFFER_SIZE) {
            mapInBlockchain.addChanges(infoBuffer);
            positionToAddInfo = 0;
        }

        Block block = StaticQueuesWrapper.getBlockChangeC();
        if (block == null) {
            flush();
            throw new QueueClosedException();
        }

        infoBuffer[positionToAddInfo] = block.getBlockInfoObject();
        positionToAddInfo++;
    }

    public void flush() throws Throwable {
        mapInBlockchain.addChanges(Arrays.copyOfRange(infoBuffer, 0, positionToAddInfo));
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
