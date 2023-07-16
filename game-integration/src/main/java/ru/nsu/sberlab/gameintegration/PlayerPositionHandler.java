package ru.nsu.sberlab.gameintegration;

import ru.nsu.sberlab.gameintegration.data.PlayerPosition;

public class PlayerPositionHandler {

    private static native PlayerPosition getPlayerPositionC();

    /**
     * Получает состояние игрока из блокчейна.
     *
     * @return объект PlayerPosition с изменением состояния
     */
    public static PlayerPosition getPlayerPosition(){
        //TODO функция чтобы взять из блокчейна местоположение
        return new PlayerPosition(0,0,0,0, 0);
    }

    /**
     * Получает состояние игрока и отправляет его на запись в блокчейн.
     */
    public static void setPlayerPosition(){
        PlayerPosition state = getPlayerPositionC();
        if (state == null) return;
        //TODO отправить на запись положение
    }
}
