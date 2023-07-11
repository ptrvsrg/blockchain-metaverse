package ru.nsu.sberlab.cprogramintegration;

/**
 * Класс для получения данных из программы на си
 * с помощью нативных методов.
 */
public class CDataExtractor {

    private static native Block getBlockChangeC();
    private static native State getStateChangeC();

    /**
     * Получает изменение блока и отправляет его на запись в блокчейн.
     */
    public static void getBlockChange(){
        Block block = getBlockChangeC();
        if (block == null) return;
        //TODO отправить на запись блок
    }

    /**
     * Получает изменение состояния и отправляет его на запись в блокчейн.
     */
    public static void getStateChange(){
        State state = getStateChangeC();
        if (state == null) return;
        //TODO отправить на запись положение
    }
}
