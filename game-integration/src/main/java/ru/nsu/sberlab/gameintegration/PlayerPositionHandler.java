package ru.nsu.sberlab.gameintegration;

import ru.nsu.sberlab.gameintegration.data.PlayerPosition;

public class PlayerPositionHandler {

    public static native PlayerPosition getPlayerPositionC();

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
     *
     * @param playerPosition состояние игрока
     */
    public static void setPlayerPosition(PlayerPosition playerPosition){
        if (playerPosition == null) return;
        //TODO отправить на запись положение
    }
}
