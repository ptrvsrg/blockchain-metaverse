package ru.nsu.sberlab.contracts.statecontract;

import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.*;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.contracts.ContractManagement;

/**
 * Класс PlayerPositionContract представляет контракт для управления позицией игрока в игровой системе.
 */
@DisplayName("${Name}")
public class PlayerPositionContract {

    private static final byte[] contractOwnerKey = new byte[]{0x00};

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
     * @param playerHash        hash игрока, чьи координаты мы хотим установить
     * @param serializableCords координаты игрока в сериализованном виде
     * @throws Exception если размер serializableCords не равен 20( 5 int'ов) или hash подписавшего транзакцию не равен playerHash
     */
    public static void putCords(Hash160 playerHash, byte[] serializableCords) throws Exception {
        if (serializableCords.length != 20) {
            throw new Exception("serializableCords array size must be 20");
        }
        if (Runtime.checkWitness(playerHash)) {
            throw new Exception("No authorization");
        }

        Storage.put(Storage.getStorageContext(), playerHash.toByteString(), serializableCords);
    }


    private static Hash160 contractOwner() {
        return Storage.getHash160(Storage.getReadOnlyContext(), contractOwnerKey);
    }


    /**
     * Метод для получения координат игрока.
     *
     * @param playerHash hash игрока, чьи координаты мы хотим узнать
     * @return координаты игрока
     */
    public static byte[] getCords(Hash160 playerHash) throws Exception {

        return Storage.getByteArray(Storage.getStorageContext(), playerHash.toByteArray());
    }

    public static void destroy() throws Exception {
        if (Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
        }

        new ContractManagement().destroy();
    }

    public static void update(ByteString nefFile, String manifest, Object data) throws Exception {
        if (Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
        }

        if (data == null) {
            new ContractManagement().update(nefFile, manifest);
        } else {
            new ContractManagement().update(nefFile, manifest, data);
        }

    }

    public static void update(ByteString nefFile, String manifest) throws Exception {
        update(nefFile, manifest, null);
    }

}
