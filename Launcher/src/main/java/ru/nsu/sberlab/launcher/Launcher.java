package ru.nsu.sberlab.launcher;

import lombok.extern.log4j.Log4j2;
import ru.nsu.sberlab.cprogramintegration.BlockchainHandler;

@Log4j2
public class Launcher {
    private native static void start();

    public static void main(String[] args) {
        log.info("LAUNCHING GAME ...");
        start();

        log.info("SENDING PREVIOUS STATE...");
        BlockchainHandler.sendStateChange();

        log.info("END SESSION");
    }
}
