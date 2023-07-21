package ru.nsu.sberlab.contracts;

import io.neow3j.types.Hash160;

import static java.lang.String.format;

public class MapContractAlreadyExist extends Exception {
    final private Hash160 mapContractExistHash;

    final private String Name;


    public MapContractAlreadyExist(Hash160 mapContractExistHash, String name) {
        super(format("Map contract with name:'%s' already exist. Hash: %s.", name, mapContractExistHash.toString()));
        this.mapContractExistHash = mapContractExistHash;
        Name = name;
    }

    public Hash160 getMapContractExistHash() {
        return mapContractExistHash;
    }


    public String getName() {
        return Name;
    }
}
