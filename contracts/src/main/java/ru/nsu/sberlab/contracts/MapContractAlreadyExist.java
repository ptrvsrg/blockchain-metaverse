package ru.nsu.sberlab.contracts;

import static java.lang.String.format;

import io.neow3j.types.Hash160;

/**
 * Класс исключения, выбрасываемого при попытке развернуть контракт MapChangesContract, но контракт
 * с таким именем уже существует в блокчейне.
 */
public class MapContractAlreadyExist
    extends Exception {

    final private Hash160 mapContractExistHash;

    final private String Name;

    /**
     * Конструктор класса MapContractAlreadyExist.
     *
     * @param mapContractExistHash Хэш ScriptHash существующего контракта с таким именем.
     * @param name                 Название контракта, который уже существует.
     */
    public MapContractAlreadyExist(Hash160 mapContractExistHash, String name) {
        super(format("Map contract with name:'%s' already exist. Hash: %s.", name,
                     mapContractExistHash.toString()));
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
