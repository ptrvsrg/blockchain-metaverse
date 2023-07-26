package ru.nsu.sberlab.gameintegration.tasks;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import ru.nsu.sberlab.blockchainintegration.MapInteraction;
import ru.nsu.sberlab.blockchainintegration.utils.BlockInfo;
import ru.nsu.sberlab.gameintegration.StaticQueuesWrapper;
import ru.nsu.sberlab.gameintegration.data.Block;

/**
 * Класс BlockchainDataRequestTask представляет поток для запроса данных из блокчейна и
 * взаимодействия с кодом на языке C/C++. Имплементирует класс Runnable.
 **/
@Log4j2
public class BlockchainDataRequestTask
    implements Runnable {

    private static final int REQUEST_MAX_SIZE = 1000;
    private final MapInteraction mapInBlockchain;
    private int takenChangesNumber = 0;


    public BlockchainDataRequestTask(MapInteraction mapInBlockchain) {
        this.mapInBlockchain = mapInBlockchain;
    }

    /**
     * Отправляет уведомление об изменении блока в блокчейне.
     */
    public void sendBlockChange()
        throws Throwable {
        ArrayList<BlockInfo> changes = mapInBlockchain.getRangeChanges(takenChangesNumber,
                                                                       REQUEST_MAX_SIZE);
        takenChangesNumber += changes.size();

        for (BlockInfo blockInfo : changes) {
            Block block = new Block(blockInfo);
            StaticQueuesWrapper.sendBlockChangeC(block);
        }

    }

    /**
     * Запускает выполнение задачи. Бесконечно запрашивает изменения данных из блокчейна и передает
     * полученные данные в программу на си. В случае прерывания потока выбрасывает исключение
     * InterruptedException.
     */
    @Override
    public void run() {
        while (true) {
            try {
                sendBlockChange();
            } catch (InterruptedIOException e) {
                return;
            } catch (Throwable e) {
                log.catching(Level.ERROR, e);
            }
        }
    }
}
