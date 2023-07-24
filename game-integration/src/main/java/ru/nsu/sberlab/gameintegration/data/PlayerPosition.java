package ru.nsu.sberlab.gameintegration.data;

import lombok.Value;
import ru.nsu.sberlab.blockchain_interaction.utils.PlayerCoordinates;

@Value
public class PlayerPosition {
    float x;
    float y;
    float z;
    float rx;
    float ry;

    @Override
    public String toString() {
        return "PlayerPosition{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", rx=" + rx +
                ", ry=" + ry +
                '}';
    }

    public PlayerPosition(float x, float y, float z, float rx, float ry) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rx = rx;
        this.ry = ry;
    }

    public PlayerCoordinates getPlayerCoordinates() {
        return new PlayerCoordinates(rx, ry, x, y, z);
    }

    public PlayerPosition(PlayerCoordinates cords) {
        x = cords.getX();
        y = cords.getY();
        z = cords.getZ();
        rx = cords.getRx();
        ry = cords.getRy();
    }
}
