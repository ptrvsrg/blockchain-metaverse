package ru.nsu.sberlab.blockchainintegration;

import io.neow3j.contract.NefFile;
import io.neow3j.crypto.ECKeyPair;
import io.neow3j.protocol.core.stackitem.StackItem;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.types.Hash256;
import io.neow3j.wallet.Account;
import java.util.ArrayList;
import ru.nsu.sberlab.blockchainintegration.utils.BlockInfo;
import ru.nsu.sberlab.blockchainintegration.utils.NodeInteraction;
import ru.nsu.sberlab.blockchainintegration.utils.PlayerCoordinates;

/**
 * Класс MapInteraction предоставляет методы для вызова функций контрактов MapChangesContract и
 * PlayerPositionContract.
 */
public class MapInteraction {

    private static final String PUT_CHANGES_FUNCTION = "putChanges";
    private static final String GET_ALL_CHANGES = "getAllChanges";

    private static final String GET_RANGE = "getRangeOfChanges";

    private static final String GET_CHANGES_WITHOUT_FIRST_N = "getChangesWithoutFirstN";

    private static final String CLEAR_MAP = "clear";

    private static final String PUT_COORDINATES = "putCords";

    private static final String GET_COORDINATES = "getCords";

    private final NodeInteraction nodeInteraction;
    private final Hash160 mapContractHash;
    private final Hash160 stateContractHash;


    /**
     * Конструктор для взаимодействия с уже существующими картами (контрактами MapChangesContract и
     * PlayerPositionContract).
     *
     * @param nodeInteraction   класс для подключения к ноде
     * @param mapContractHash   Hash развернутого контракта MapChangesContract
     * @param stateContractHash Hash развернутого контракта PlayerPositionContract
     */
    public MapInteraction(NodeInteraction nodeInteraction, Hash160 mapContractHash,
                          Hash160 stateContractHash) {
        this.nodeInteraction = nodeInteraction;
        this.mapContractHash = mapContractHash;
        this.stateContractHash = stateContractHash;
    }

    /**
     * Конструктор создающий карту(разворачивает два контракта MapChangesContract и
     * PlayerPositionContract).
     *
     * @param httpUrl           http адрес блокчейн ноды
     * @param account           аккаунт от которого производятся взаимодействия
     * @param mapContractHash   Hash развернутого контракта MapChangesContract
     * @param stateContractHash Hash развернутого контракта PlayerPositionContract
     */
    public MapInteraction(String httpUrl, Account account, Hash160 mapContractHash,
                          Hash160 stateContractHash) {
        this(new NodeInteraction(httpUrl, account), mapContractHash, stateContractHash);
    }

    /**
     * Конструктор создающий карту(разворачивает два контракта MapChangesContract и
     * PlayerPositionContract).
     *
     * @param httpUrl           http адрес блокчейн ноды
     * @param keyPair           пара ключей публичный/приватный для аккаунта
     * @param mapContractHash   Hash развернутого контракта MapChangesContract
     * @param stateContractHash Hash развернутого контракта PlayerPositionContract
     */
    public MapInteraction(String httpUrl, ECKeyPair keyPair, Hash160 mapContractHash,
                          Hash160 stateContractHash) {
        this(new NodeInteraction(httpUrl, keyPair), mapContractHash, stateContractHash);
    }

    /**
     * Конструктор создающий карту(разворачивает два контракта MapChangesContract и
     * PlayerPositionContract).
     *
     * @param httpUrl           http адрес блокчейн ноды
     * @param mapContractHash   Hash развернутого контракта MapChangesContract
     * @param stateContractHash Hash развернутого контракта PlayerPositionContract
     */
    public MapInteraction(String httpUrl, Hash160 mapContractHash, Hash160 stateContractHash) {
        this(new NodeInteraction(httpUrl), mapContractHash, stateContractHash);
    }


    /**
     * Добавить изменения.
     *
     * @param infoArray Изменения для добавления
     * @throws Throwable бросается в случае если не получается вызвать функцию putChanges контракта
     */
    public void addChanges(BlockInfo... infoArray)
        throws Throwable {
        byte[] infoListSerialized = new byte[BlockInfo.BlockInfoByteSize * infoArray.length];

        for (int i = 0; i < infoArray.length; i++) {
            System.arraycopy(infoArray[i].serialize(), 0, infoListSerialized,
                             i * BlockInfo.BlockInfoByteSize, BlockInfo.BlockInfoByteSize);
        }

        nodeInteraction.invokeFunctionInContract(mapContractHash, PUT_CHANGES_FUNCTION,
                                                 ContractParameter.byteArray(infoListSerialized));

    }

    public Hash256 addChangesNoBlocking(BlockInfo... infoArray)
        throws Throwable {
        byte[] infoListSerialized = new byte[BlockInfo.BlockInfoByteSize * infoArray.length];

        for (int i = 0; i < infoArray.length; i++) {
            System.arraycopy(infoArray[i].serialize(), 0, infoListSerialized,
                             i * BlockInfo.BlockInfoByteSize, BlockInfo.BlockInfoByteSize);
        }

        return nodeInteraction.invokeFunctionNoBlocking(mapContractHash, PUT_CHANGES_FUNCTION,
                                                        ContractParameter.byteArray(
                                                            infoListSerialized));

    }

    /**
     * Получить все изменения.
     *
     * @return все изменения
     * @throws Throwable бросается в случае если не получается вызвать функцию getAllChanges
     *                   контракта
     */
    public ArrayList<BlockInfo> getAllChanges()
        throws Throwable {
        byte[] result = nodeInteraction.invokeFunctionInContract(mapContractHash, GET_ALL_CHANGES)
                                       .getByteArray();

        return BlockInfo.getInfoArrayFromByteRepresentation(result);
    }

    /**
     * Возвращает все изменения без первых N.
     *
     * @return все изменения без первых N
     * @throws Throwable бросается в случае если не получается вызвать функцию
     *                   getChangesWithoutFirstN контракта
     */
    public ArrayList<BlockInfo> getAllChangesWithoutFirstN(int N)
        throws Throwable {
        byte[] result = nodeInteraction.invokeFunctionInContract(mapContractHash,
                                                                 GET_CHANGES_WITHOUT_FIRST_N,
                                                                 ContractParameter.integer(N))
                                       .getByteArray();

        return BlockInfo.getInfoArrayFromByteRepresentation(result);
    }

    /**
     * Удалить все изменения.
     *
     * @throws Throwable бросается в случае если не получается вызвать функцию clear контракта
     */
    public void deleteAllChanges()
        throws Throwable {
        nodeInteraction.invokeFunctionInContract(mapContractHash, CLEAR_MAP);
    }


    public ArrayList<BlockInfo> getRangeChanges(int i, int n)
        throws Throwable {
        byte[] result = nodeInteraction.invokeFunctionInContract(mapContractHash, GET_RANGE,
                                                                 ContractParameter.integer(i),
                                                                 ContractParameter.integer(n))
                                       .getByteArray();

        return BlockInfo.getInfoArrayFromByteRepresentation(result);
    }

    /**
     * Положить координаты игрока.
     *
     * @param coordinates координаты
     * @throws Throwable бросается в случае если не получается вызвать функцию putCords контракта
     */
    public void putPlayerCoordinates(PlayerCoordinates coordinates)
        throws Throwable {

        nodeInteraction.invokeFunctionInContract(stateContractHash, PUT_COORDINATES,
                                                 ContractParameter.hash160(
                                                     nodeInteraction.getAccount()
                                                                    .getScriptHash()),
                                                 ContractParameter.byteArray(
                                                     coordinates.serialize()));
    }

    /**
     * Получить координаты игрока.
     *
     * @return координаты игрока
     * @throws Throwable бросается в случае если не получается вызвать функцию getCords контракта
     */
    public PlayerCoordinates getCoordinates()
        throws Throwable {
        return new PlayerCoordinates(
            nodeInteraction.invokeFunctionInContract(stateContractHash, GET_COORDINATES,
                                                     ContractParameter.hash160(
                                                         nodeInteraction.getAccount()
                                                                        .getScriptHash()))
                           .getByteArray());
    }

    /**
     * Разрушение карты.
     *
     * @throws Throwable если не получилось вызвать соответствующие методы
     */
    public void destroyMap()
        throws Throwable {
        nodeInteraction.invokeFunctionInContract(mapContractHash, "destroy");
        nodeInteraction.invokeFunctionInContract(stateContractHash, "destroy");
    }

    /**
     * Обновить контракт MapChangesContract.
     *
     * @param nefFile  скомпилированный контракт
     * @param manifest манифест
     */
    public void updateMapContract(NefFile nefFile, String manifest)
        throws Throwable {
        updateMapContract(nefFile, manifest, null);
    }

    /**
     * Обновить контракт MapChangesContract.
     *
     * @param nefFile  скомпилированный контракт
     * @param manifest манифест
     * @param data     передаваемые параметры
     */
    public void updateMapContract(NefFile nefFile, String manifest, Object data)
        throws Throwable {
        if (data == null) {
            nodeInteraction.invokeFunctionInContract(mapContractHash, "update",
                                                     ContractParameter.byteArray(nefFile.toArray()),
                                                     ContractParameter.string(manifest));
        } else {
            nodeInteraction.invokeFunctionInContract(mapContractHash, "update",
                                                     ContractParameter.byteArray(nefFile.toArray()),
                                                     ContractParameter.string(manifest),
                                                     ContractParameter.any(data));
        }
    }

    /**
     * Обновить контракт PlayerPositionContract.
     *
     * @param nefFile  скомпилированный контракт
     * @param manifest манифест
     */
    public void updateStateContract(NefFile nefFile, String manifest)
        throws Throwable {
        updateStateContract(nefFile, manifest, null);
    }

    /**
     * Обновить контракт PlayerPositionContract.
     *
     * @param nefFile  скомпилированный контракт
     * @param manifest манифест
     * @param data     передаваемые параметры
     */
    public void updateStateContract(NefFile nefFile, String manifest, Object data)
        throws Throwable {
        if (data == null) {
            nodeInteraction.invokeFunctionInContract(stateContractHash, "update",
                                                     ContractParameter.byteArray(nefFile.toArray()),
                                                     ContractParameter.string(manifest));
        } else {
            nodeInteraction.invokeFunctionInContract(stateContractHash, "update",
                                                     ContractParameter.byteArray(nefFile.toArray()),
                                                     ContractParameter.string(manifest),
                                                     ContractParameter.any(data));
        }
    }

    public StackItem getResult(Hash256 transactionHash)
        throws Exception {
        return nodeInteraction.getResult(transactionHash);
    }

    public NodeInteraction getNodeInteraction() {
        return nodeInteraction;
    }

    public Hash160 getMapContractHash() {
        return mapContractHash;
    }

    public Hash160 getStateContractHash() {
        return stateContractHash;
    }
}
