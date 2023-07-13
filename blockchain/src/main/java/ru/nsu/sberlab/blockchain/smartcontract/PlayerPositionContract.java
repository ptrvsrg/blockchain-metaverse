package ru.nsu.sberlab.blockchain.smartcontract;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.contracts.ContractInterface;

/**
 * Класс PlayerPositionContract представляет контракт для управления позицией игрока в игровой системе.
 * Расширяет класс ContractInterface для реализации функций контракта.
 */
public class PlayerPositionContract extends ContractInterface {

    private static final byte[] contractOwnerKey = new byte[]{0x00};

    private static final byte[] playerCordsKey = new byte[]{0x01};

    public PlayerPositionContract(Hash160 contractHash) {
        super(contractHash);
    }

    /**
     * Метод развертывания контракта.
     *
     * @param data ScriptHash аккаунта владельца
     * @throws Exception ScriptHash не указан
     */
    @OnDeployment
    public static void deploy(Object data, boolean _) throws Exception {
        StorageContext ctx = Storage.getStorageContext();

        if (!(data instanceof Hash160)) {
            throw new Exception("No authorization");
        }

        Storage.put(ctx, contractOwnerKey, (Hash160) data);


    }


    /**
     * Метод для установки координат игрока.
     *
     * @param serializableCords координаты игрока в сериализованном виде
     * @throws Exception если размер serializableCords не равен 12( 3 int'а) или аккаунт вызвавший функцию не является владельцем контракта
     */
    public static void putCords(byte[] serializableCords) throws Exception {
        if (serializableCords.length != 12) {
            throw new Exception("serializableCords array size must be 12");
        }
        if (Runtime.getCallingScriptHash().equals(contractOwner())) {
            throw new Exception("No authorization");
        }

        Storage.put(Storage.getStorageContext(), playerCordsKey, serializableCords);
    }


    private static Hash160 contractOwner() {
        return Storage.getHash160(Storage.getReadOnlyContext(), contractOwnerKey);
    }


    /**
     * Метод для получения координат игрока.
     *
     * @return координаты игрока
     * @throws Exception если аккаунт вызвавший функцию не является владельцем контракта
     */
    public static byte[] getCords() throws Exception {
        if (Runtime.getCallingScriptHash().equals(contractOwner())) {
            throw new Exception("No authorization");
        }

        return Storage.getByteArray(Storage.getStorageContext(), playerCordsKey);
    }
}
