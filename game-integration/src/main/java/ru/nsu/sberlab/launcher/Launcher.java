package ru.nsu.sberlab.launcher;

import lombok.extern.log4j.Log4j2;
import ru.nsu.sberlab.cprogramintegration.DataChangeRequestTask;

@Log4j2
public class Launcher {
    private native static void start();

    public static void main(String[] args) {
        log.info("LAUNCHING GAME ...");
        start();

        log.info("START DATA CHANGE REQUEST TASK...");
        new DataChangeRequestTask();

        log.info("END SESSION");
    }
}
