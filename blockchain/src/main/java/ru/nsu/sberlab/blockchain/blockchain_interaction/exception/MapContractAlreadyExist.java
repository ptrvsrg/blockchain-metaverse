package ru.nsu.sberlab.blockchain.blockchain_interaction.exception;

import io.neow3j.types.Hash160;

import static java.lang.String.format;

public class MapContractAlreadyExist extends BlockChainException {
    final private Hash160 existHash;

    final private String Name;


    public MapContractAlreadyExist(Hash160 existHash, String name) {
        super(format("Map contract with name:'%s' already exist. Its hash: %s", name, existHash.toString()));
        this.existHash = existHash;
        Name = name;
    }

    public Hash160 getExistHash() {
        return existHash;
    }

    public String getName() {
        return Name;
    }
}
