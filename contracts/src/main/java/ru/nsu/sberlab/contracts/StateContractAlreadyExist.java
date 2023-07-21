package ru.nsu.sberlab.contracts;

import io.neow3j.types.Hash160;

import static java.lang.String.format;

public class StateContractAlreadyExist extends Exception{
    final private Hash160 stateContractExistHash;

    final private String Name;


    public StateContractAlreadyExist(Hash160 stateContractExistHash, String name) {
        super(format("State contract with name:'%s' already exist. Hash: %s.", name, stateContractExistHash.toString()));
        this.stateContractExistHash = stateContractExistHash;
        Name = name;
    }

    public Hash160 getStateContractExistHash() {
        return stateContractExistHash;
    }


    public String getName() {
        return Name;
    }
}
