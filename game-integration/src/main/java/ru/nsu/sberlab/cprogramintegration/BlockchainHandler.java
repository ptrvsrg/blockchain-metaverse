package ru.nsu.sberlab.cprogramintegration;

/**
 * Класс BlockchainHandler обрабатывает изменения в блокчейне и отправляет уведомления
 * в программу на си.
 */
public class BlockchainHandler {
    // Нативные методы для взаимодействия с кодом на языке C/C++
    private static native void sendBlockChangeC(int p, int q, int x, int y, int z, int w);
    private static native void sendStateChangeC(int x, int y, int z, int rx, int rz);

    /**
     * Отправляет уведомление об изменении блока в блокчейне.
     */
    public void sentBlockChange(){
        //TODO функция чтобы взять из блокчейна блок
        Block block = new Block(0,0,0,0,0,0);
        sendBlockChangeC(block.getP(), block.getQ(),
                block.getX(), block.getY(), block.getZ(), block.getW());
    }

    /**
     * Отправляет уведомление об изменении состояния в блокчейне.
     */
    public static void sendStateChange(){
        //TODO функция чтобы взять из блокчейна местоположение
        State state = new State(0,0,0,0,0);
        sendStateChangeC(state.getX(), state.getY(), state.getZ(), state.getRx(), state.getRy());
    }

    /**
     * Отправляет сообщение об неудачной транзакции в блокчейне
     * (удаляет несохранившийся блок)
     */
    public static void sendTransactionFailureMessage(){
        //TODO функция чтобы взять из блокчейна блок
        Block lastBlock = new Block(0,0,0,0,0,0);
        sendBlockChangeC(lastBlock.getP(), lastBlock.getQ(),
                lastBlock.getX(), lastBlock.getY(), lastBlock.getZ(), 0);
    }
}
