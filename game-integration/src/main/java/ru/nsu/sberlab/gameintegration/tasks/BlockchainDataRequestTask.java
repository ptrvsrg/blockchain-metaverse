package ru.nsu.sberlab.gameintegration.tasks;

import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
import ru.nsu.sberlab.blockchain_interaction.utils.BlockInfo;
import ru.nsu.sberlab.gameintegration.StaticQueuesWrapper;
import ru.nsu.sberlab.gameintegration.data.Block;

import java.io.InterruptedIOException;
import java.util.ArrayList;

/**
 * Класс BlockchainDataRequestTask представляет поток для запроса данных из блокчейна и
 * взаимодействия с кодом на языке C/C++. Имплементирует класс Runnable.
 **/

public class BlockchainDataRequestTask implements Runnable {

    /**
     * Время между запросами на изменение данных в блокчейне (в миллисекундах).
     */
    private static final int TIME_REQUEST = 1500;

    private static final int REQUEST_MAX_SIZE = 1000;

    private int takenChangesNumber = 0;

    private final MapInteraction mapInBlockchain;


    public BlockchainDataRequestTask(MapInteraction mapInBlockchain) {
        this.mapInBlockchain = mapInBlockchain;
    }

    /**
     * Отправляет уведомление об изменении блока в блокчейне.
     */
    public void sendBlockChange() throws Throwable {
        ArrayList<BlockInfo> changes = mapInBlockchain.getRangeChanges(takenChangesNumber, REQUEST_MAX_SIZE);
        takenChangesNumber += changes.size();

        for (BlockInfo blockInfo : changes) {
            Block block = new Block(blockInfo);
            StaticQueuesWrapper.sendBlockChangeC(block);
        }

    }

    /**
     * Запускает выполнение задачи.
     * Бесконечно запрашивает изменения данных из блокчейна и передает полученные
     * данные в программу на си. В случае прерывания потока выбрасывает исключение InterruptedException.
     */
    @Override
    public void run() {
        while (true) {
            try {
                sendBlockChange();
            }
            catch (InterruptedIOException e) {
                return;
            }
            catch (Throwable ignore) {

            }
        }
    }
}
