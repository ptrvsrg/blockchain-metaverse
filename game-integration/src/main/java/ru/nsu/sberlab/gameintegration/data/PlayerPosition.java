package ru.nsu.sberlab.gameintegration.data;

import lombok.AllArgsConstructor;
import lombok.Value;
import ru.nsu.sberlab.blockchainintegration.utils.PlayerCoordinates;

@Value
@AllArgsConstructor
public class PlayerPosition {

    float x;
    float y;
    float z;
    float rx;
    float ry;

    public PlayerPosition(PlayerCoordinates cords) {
        x = cords.getX();
        y = cords.getY();
        z = cords.getZ();
        rx = cords.getRx();
        ry = cords.getRy();
    }

    @Override
    public String toString() {
        return "PlayerPosition{" + "x=" + x + ", y=" + y + ", z=" + z + ", rx=" + rx + ", ry=" +
               ry + '}';
    }

    public PlayerCoordinates getPlayerCoordinates() {
        return new PlayerCoordinates(rx, ry, x, y, z);
    }
}
