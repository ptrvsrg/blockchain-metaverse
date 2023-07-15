package ru.nsu.sberlab.gameintegration.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerPosition {
    private int x;
    private int y;
    private int z;
    private int rx;
    private int ry;
}
