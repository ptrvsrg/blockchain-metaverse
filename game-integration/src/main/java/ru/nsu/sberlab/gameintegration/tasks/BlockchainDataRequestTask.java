package ru.nsu.sberlab.gameintegration.tasks;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
import ru.nsu.sberlab.blockchain_interaction.utils.BlockInfo;
import ru.nsu.sberlab.gameintegration.data.Block;

import java.util.ArrayList;

/**
 * Класс BlockchainDataRequestTask представляет поток для запроса данных из блокчейна и
 * взаимодействия с кодом на языке C/C++. Имплементирует класс Runnable.
 **/
@Log4j2
public class BlockchainDataRequestTask implements Runnable {

    /**
     * Время между запросами на изменение данных в блокчейне (в миллисекундах).
     */
    private static final int TIME_REQUEST = 1500;

    private int takenChangesNumber = 0;

    private final MapInteraction mapInBlockchain;


    public BlockchainDataRequestTask(MapInteraction mapInBlockchain) {
        this.mapInBlockchain = mapInBlockchain;
    }

    private static native void sendBlockChangeC(Block block);

    /**
     * Отправляет уведомление об изменении блока в блокчейне.
     */
    public void sendBlockChange() throws Throwable {
        ArrayList<BlockInfo> changes = mapInBlockchain.getAllChangesWithoutFirstN(takenChangesNumber);
        takenChangesNumber += changes.size();

        for (BlockInfo blockInfo : changes) {
            Block block = new Block(blockInfo);
            sendBlockChangeC(block);
        }

    }

    /**
     * Запускает выполнение задачи.
     * Бесконечно запрашивает изменения данных из блокчейна и передает полученные
     * данные в программу на си. В случае прерывания потока выбрасывает исключение InterruptedException.
     *
     * @noinspection InfiniteLoopStatement
     */
    @Override
    public void run() {
        while (true) {
            try {
                sendBlockChange();
            } catch (Throwable e) {
                log.catching(Level.ERROR, e);
            }
        }
    }
}
