package ru.nsu.sberlab.blockchain_interaction;

import io.neow3j.contract.NefFile;
import io.neow3j.transaction.exceptions.TransactionConfigurationException;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import ru.nsu.sberlab.blockchain_interaction.exception.BlockChainException;
import ru.nsu.sberlab.blockchain_interaction.exception.MapContractAlreadyExist;
import ru.nsu.sberlab.blockchain_interaction.exception.StateContractAlreadyExist;
import ru.nsu.sberlab.blockchain_interaction.utils.BlockInfo;
import ru.nsu.sberlab.blockchain_interaction.utils.Coordinates;
import ru.nsu.sberlab.blockchain_interaction.utils.NodeInteraction;
import ru.nsu.sberlab.contracts.mapcontract.MapChangesContract;

import java.util.HashMap;
import java.util.List;

/**
 * Класс MapInteraction предоставляет методы для вызова функций контрактов MapChangesContract и PlayerPositionContract.
 */
public class MapInteraction {

    private static final String PUT_CHANGES_FUNCTION = "putChanges";
    private static final String GET_ALL_CHANGES = "getAllChanges";

    private static final String GET_CHANGES_WITHOUT_FIRST_N = "getChangesWithoutFirstN";

    private static final String CLEAR_MAP = "clear";

    private static final String PUT_COORDINATES = "putCords";

    private static final String GET_COORDINATES = "getCords";
    private final NodeInteraction nodeInteraction;
    private Hash160 mapContractHash;
    private Hash160 stateContractHash;


    /**
     * Конструктор создающий карту(разворачивает два контракта MapChangesContract и PlayerPositionContract).
     *
     * @param httpUrl http адрес блокчейн ноды
     * @param account аккаунт от коготорого производятся взаимодействия
     * @param mapName название карты
     * @throws BlockChainException не удается развернуть какой либо контракт или он уже существует
     */
    public MapInteraction(String httpUrl, Account account, String mapName) throws BlockChainException {
        nodeInteraction = new NodeInteraction(httpUrl, account);
        HashMap<String, Hash160> returnMap = new HashMap<>(2);
        HashMap<String, String> replaceMap = new HashMap<>(1);
        replaceMap.put("Name", mapName);

        try {
            mapContractHash = nodeInteraction.deployContract(MapChangesContract.class.getCanonicalName(),
                    ContractParameter.hash160(nodeInteraction.getAccount().getScriptHash()), replaceMap);
        } catch (TransactionConfigurationException e) {
            if (e.getMessage().contains("Contract Already Exists: ")) {
                mapContractHash = new Hash160(e.getMessage().substring(e.getMessage().indexOf("Contract Already Exists: ")
                        + "Contract Already Exists: ".length()));
                throw new MapContractAlreadyExist(mapContractHash, mapName);
            } else {
                throw new BlockChainException("unable to create map contract", e);
            }
        } catch (Throwable e) {
            throw new BlockChainException("unable to create map contract", e);

        }

        try {
            stateContractHash = nodeInteraction.deployContract(MapChangesContract.class.getCanonicalName(),
                    ContractParameter.hash160(nodeInteraction.getAccount().getScriptHash()), replaceMap);
        } catch (TransactionConfigurationException e) {
            if (e.getMessage().contains("Contract Already Exists: ")) {
                stateContractHash = new Hash160(e.getMessage().substring(e.getMessage().indexOf("Contract Already Exists: ")
                        + "Contract Already Exists: ".length()));
                throw new StateContractAlreadyExist(stateContractHash, mapName);
            } else {
                throw new BlockChainException("unable to create state contract", e);
            }
        } catch (Throwable e) {
            throw new BlockChainException("unable to create state contract", e);

        }

    }

    /**
     * Конструктор для взаимодействия с уже существующими картами (контрактами MapChangesContract и PlayerPositionContract).
     *
     * @param httpUrl           http адрес блокчейн ноды
     * @param account           аккаунт от коготорого производятся взаимодействия
     * @param mapContractHash   Hash развенутого контракта MapChangesContract
     * @param stateContractHash Hash развенутого контракта PlayerPositionContract
     */
    public MapInteraction(String httpUrl, Account account, Hash160 mapContractHash, Hash160 stateContractHash) {
        nodeInteraction = new NodeInteraction(httpUrl, account);
        this.mapContractHash = mapContractHash;
        this.stateContractHash = stateContractHash;
    }

    /**
     * Добавить изменения.
     *
     * @param mapChangesContractHash Hash контракта mapChanges
     * @param infoArray              Изменения для добавления
     * @throws Throwable бросается в случае если не получается вызвать функцию putChanges контракта
     */
    public void addChanges(Hash160 mapChangesContractHash, BlockInfo... infoArray) throws Throwable {
        byte[] infoListSerialized = new byte[BlockInfo.BlockInfoByteSize * infoArray.length];


        for (int i = 0; i < infoArray.length; i++)
            System.arraycopy(infoArray[i].serialize(), 0, infoListSerialized,
                    i * BlockInfo.BlockInfoByteSize, BlockInfo.BlockInfoByteSize);

        nodeInteraction.invokeFunctionInContract(mapChangesContractHash, PUT_CHANGES_FUNCTION, ContractParameter.byteArray(infoListSerialized));

    }

    /**
     * Получить все изменения.
     *
     * @param mapChangesContractHash Hash контракта mapChanges
     * @return все изменения
     * @throws Throwable бросается в случае если не получается вызвать функцию getAllChanges контракта
     */
    public List<BlockInfo> getAllChanges(Hash160 mapChangesContractHash) throws Throwable {
        byte[] result = nodeInteraction.invokeFunctionInContract(mapChangesContractHash, GET_ALL_CHANGES).getByteArray();

        return BlockInfo.getInfoArrayFromByteRepresentation(result);
    }

    /**
     * Возвращает все изменения без первых N.
     *
     * @param mapChangesContractHash Hash контракта mapChanges
     * @return все изменения без первых N
     * @throws Throwable бросается в случае если не получается вызвать функцию getChangesWithoutFirstN контракта
     */
    public List<BlockInfo> getAllChangesWithoutFirstN(Hash160 mapChangesContractHash, int N) throws Throwable {
        byte[] result = nodeInteraction.invokeFunctionInContract(mapChangesContractHash, GET_CHANGES_WITHOUT_FIRST_N,
                ContractParameter.integer(N)).getByteArray();

        return BlockInfo.getInfoArrayFromByteRepresentation(result);
    }

    /**
     * Удалить все изменения.
     *
     * @param mapChangesContractHash Hash контракта mapChanges
     * @throws Throwable бросается в случае если не получается вызвать функцию clear контракта
     */
    public void deleteAllChanges(Hash160 mapChangesContractHash) throws Throwable {
        nodeInteraction.invokeFunctionInContract(mapChangesContractHash, CLEAR_MAP);
    }

    /**
     * Положить координаты игрока.
     *
     * @param stateContractHash Hash контракта PlayerPositionContract
     * @param coordinates       координаты
     * @throws Throwable бросается в случае если не получается вызвать функцию putCords контракта
     */
    public void putPlayerCoordinates(Hash160 stateContractHash, Coordinates coordinates) throws Throwable {


        nodeInteraction.invokeFunctionInContract(stateContractHash, PUT_COORDINATES, ContractParameter.hash160(nodeInteraction.getAccount().getScriptHash()),
                ContractParameter.byteArray(coordinates.serialize()));
    }

    /**
     * Получить координаты игрока.
     *
     * @param stateContractHash Hash контракта PlayerPositionContract
     * @return координаты игрока
     * @throws Throwable бросается в случае если не получается вызвать функцию getCords контракта
     */
    public Coordinates getCoordinates(Hash160 stateContractHash) throws Throwable {
        return new Coordinates(nodeInteraction.invokeFunctionInContract(stateContractHash, GET_COORDINATES,
                ContractParameter.hash160(nodeInteraction.getAccount().getScriptHash())).getByteArray());
    }

    /**
     * Разрушение карты.
     *
     * @throws Throwable если не получилось вызвать соответствующие методы
     */
    public void destroyMap() throws Throwable {
        nodeInteraction.invokeFunctionInContract(mapContractHash, "destroy");
        nodeInteraction.invokeFunctionInContract(stateContractHash, "destroy");
    }

    /**
     * Обновить контракт MapChangesContract.
     *
     * @param nefFile  скомпилированный контракт
     * @param manifest манифест
     */
    public void updateMapContract(NefFile nefFile, String manifest) throws Throwable {
        updateMapContract(nefFile, manifest, null);
    }

    /**
     * Обновить контракт MapChangesContract.
     *
     * @param nefFile  скомпилированный контракт
     * @param manifest манифест
     * @param data     передаваемые параметры
     */
    public void updateMapContract(NefFile nefFile, String manifest, Object data) throws Throwable {
        if (data == null) {
            nodeInteraction.invokeFunctionInContract(mapContractHash, "update",
                    ContractParameter.byteArray(nefFile.toArray()), ContractParameter.string(manifest));
        } else {
            nodeInteraction.invokeFunctionInContract(mapContractHash, "update",
                    ContractParameter.byteArray(nefFile.toArray()), ContractParameter.string(manifest),
                    ContractParameter.any(data));
        }
    }

    /**
     * Обновить контракт PlayerPositionContract.
     *
     * @param nefFile  скомпилированный контракт
     * @param manifest манифест
     */
    public void updateStateContract(NefFile nefFile, String manifest) throws Throwable {
        updateStateContract(nefFile, manifest, null);
    }

    /**
     * Обновить контракт PlayerPositionContract.
     *
     * @param nefFile  скомпилированный контракт
     * @param manifest манифест
     * @param data     передаваемые параметры
     */
    public void updateStateContract(NefFile nefFile, String manifest, Object data) throws Throwable {
        if (data == null) {
            nodeInteraction.invokeFunctionInContract(stateContractHash, "update",
                    ContractParameter.byteArray(nefFile.toArray()), ContractParameter.string(manifest));
        } else {
            nodeInteraction.invokeFunctionInContract(stateContractHash, "update",
                    ContractParameter.byteArray(nefFile.toArray()), ContractParameter.string(manifest),
                    ContractParameter.any(data));
        }
    }


    public Hash160 getMapContractHash() {
        return mapContractHash;
    }

    public Hash160 getStateContractHash() {
        return stateContractHash;
    }
}
