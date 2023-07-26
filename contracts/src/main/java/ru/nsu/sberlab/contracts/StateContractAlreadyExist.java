package ru.nsu.sberlab.contracts;

import static java.lang.String.format;

import io.neow3j.types.Hash160;

/**
 * Класс исключения, выбрасываемого при попытке развернуть контракт PlayerPositionContract, но
 * контракт с таким именем уже существует в блокчейне.
 */
public class StateContractAlreadyExist
    extends Exception {

    final private Hash160 stateContractExistHash;
    final private String Name;

    /**
     * Конструктор класса StateContractAlreadyExist.
     *
     * @param stateContractExistHash Хэш ScriptHash существующего контракта с таким именем.
     * @param name                   Название контракта, который уже существует.
     */
    public StateContractAlreadyExist(Hash160 stateContractExistHash, String name) {
        super(format("State contract with name:'%s' already exist. Hash: %s.", name,
                     stateContractExistHash.toString()));
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
