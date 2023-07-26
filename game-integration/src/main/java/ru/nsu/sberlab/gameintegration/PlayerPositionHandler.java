package ru.nsu.sberlab.gameintegration;

import ru.nsu.sberlab.blockchainintegration.MapInteraction;
import ru.nsu.sberlab.blockchainintegration.utils.PlayerCoordinates;
import ru.nsu.sberlab.gameintegration.data.PlayerPosition;

public class PlayerPositionHandler {

    public static native PlayerPosition getPlayerPositionC();

    /**
     * Получает состояние игрока из блокчейна.
     *
     * @return объект PlayerPosition с изменением состояния
     */
    public static PlayerPosition getPlayerPosition(MapInteraction mapInBlockchain)
        throws Throwable {
        PlayerCoordinates cords = mapInBlockchain.getCoordinates();
        return new PlayerPosition(cords);
    }

    /**
     * Получает состояние игрока и отправляет его на запись в блокчейн.
     *
     * @param playerPosition состояние игрока
     */
    public static void setPlayerPosition(MapInteraction mapInBlockchain,
                                         PlayerPosition playerPosition)
        throws Throwable {
        if (playerPosition == null) {
            return;
        }
        mapInBlockchain.putPlayerCoordinates(playerPosition.getPlayerCoordinates());
    }
}
