package ru.nsu.sberlab.blockchain.blockchain_interaction.exception;

import io.neow3j.types.Hash160;

import static java.lang.String.format;

public class MapAlreadyExist extends BlockChainException {
    final private Hash160 mapContractExistHash;
    final private Hash160 stateContractExistHash;

    final private String Name;


    public MapAlreadyExist(Hash160 mapContractExistHash, Hash160 stateContractExistHash, String name) {
        super(format("Map with name:'%s' already exist. Map hash: %s, state hash: %s", name, mapContractExistHash.toString(), stateContractExistHash.toString()));
        this.mapContractExistHash = mapContractExistHash;
        this.stateContractExistHash = stateContractExistHash;
        Name = name;
    }

    public Hash160 getMapContractExistHash() {
        return mapContractExistHash;
    }

    public Hash160 getStateContractExistHash() {
        return stateContractExistHash;
    }

    public String getName() {
        return Name;
    }
}
