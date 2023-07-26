package ru.nsu.sberlab.gameintegration;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import ru.nsu.sberlab.blockchainintegration.MapInteraction;
import ru.nsu.sberlab.gameintegration.data.Block;
import ru.nsu.sberlab.gameintegration.data.TransactionInfo;
import ru.nsu.sberlab.gameintegration.tasks.BlockchainDataRequestTask;
import ru.nsu.sberlab.gameintegration.tasks.CDataRequestTask;
import ru.nsu.sberlab.gameintegration.tasks.CheckTransactionsTask;
import ru.nsu.sberlab.gameintegration.tasks.SendChangesToBlockchainTask;
import ru.nsu.sberlab.gameintegration.tasks.StartTask;

/**
 * Класс Launcher представляет запуск игры и выполнения задач для запроса изменений данных.
 */
@Log4j2
public class Launcher {

    static {
        System.loadLibrary("glew");
        System.loadLibrary("lodepng");
        System.loadLibrary("noise");
        System.loadLibrary("sqlite");
        System.loadLibrary("tinycthread");
        System.loadLibrary("craft");
        System.loadLibrary("jnative");

        StaticQueuesWrapper.init();
    }

    /**
     * Запускает игру и выполняет задачи для запроса изменений данных из блокчейна.
     *
     * @param httpUrl   http адрес блокчейн ноды
     * @param keyPair   пара ключей для входа в аккаунт
     * @param mapHash   Hash контракта для карты
     * @param stateHash Hash контракта для состояния карты
     * @throws Throwable не удается установить связь с нодой или получить координаты игрока
     */
    public void launch(String httpUrl, ECKeyPair keyPair, Hash160 mapHash, Hash160 stateHash)
        throws Throwable {
        launch(new MapInteraction(httpUrl, keyPair, mapHash, stateHash));
    }

    /**
     * Запускает игру и выполняет задачи для запроса изменений данных из блокчейна.
     *
     * @param httpUrl   http адрес блокчейн ноды
     * @param mapHash   Hash контракта для карты
     * @param stateHash Hash контракта для состояния карты
     * @throws Throwable не удается установить связь с нодой или получить координаты игрока
     */
    public void launch(String httpUrl, Hash160 mapHash, Hash160 stateHash)
        throws Throwable {
        launch(new MapInteraction(httpUrl, mapHash, stateHash));
    }

    /**
     * Запускает игру и выполняет задачи для запроса изменений данных из блокчейна.
     *
     * @param httpUrl   http адрес блокчейн ноды
     * @param account   аккаунт
     * @param mapHash   Hash контракта для карты
     * @param stateHash Hash контракта для состояния карты
     * @throws Throwable не удается установить связь с нодой или получить координаты игрока
     */
    public void launch(String httpUrl, Account account, Hash160 mapHash, Hash160 stateHash)
        throws Throwable {
        launch(new MapInteraction(httpUrl, account, mapHash, stateHash));
    }


    /**
     * Запускает игру и выполняет задачи для запроса изменений данных из блокчейна.
     */
    public void launch(MapInteraction mapInBlockchain)
        throws Throwable {

        Queue<TransactionInfo> transactionInfos = new ArrayBlockingQueue<>(100);
        Queue<Block> blocksChanges = new ArrayBlockingQueue<>(100);
        Thread startTask = new Thread(
            new StartTask(PlayerPositionHandler.getPlayerPosition(mapInBlockchain)));
        Thread blockchainDataRequestTask = new Thread(
            new BlockchainDataRequestTask(mapInBlockchain));
        Thread checkBlockchainSendTask = new Thread(
            new CheckTransactionsTask(mapInBlockchain, transactionInfos));
        Thread cDataRequestTask = new Thread(new CDataRequestTask(blocksChanges));
        Thread sendChangesToBlockchainTask = new Thread(
            new SendChangesToBlockchainTask(mapInBlockchain, transactionInfos, blocksChanges));

        startTask.start();
        blockchainDataRequestTask.start();
        checkBlockchainSendTask.start();
        cDataRequestTask.start();
        sendChangesToBlockchainTask.start();

        try {
            startTask.join();
            cDataRequestTask.join();

            PlayerPositionHandler.setPlayerPosition(mapInBlockchain,
                                                    PlayerPositionHandler.getPlayerPositionC());
            blockchainDataRequestTask.interrupt();
            checkBlockchainSendTask.interrupt();
            sendChangesToBlockchainTask.interrupt();
        } catch (Throwable e) {
            log.catching(Level.ERROR, e);
        }
    }
}
