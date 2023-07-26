package ru.nsu.sberlab.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.contracts.ContractManagement;

/**
 * Класс PlayerPositionContract представляет контракт для управления позицией игрока в игровой
 * системе.
 */
@DisplayName("${Name}")
@Permission(contract = "*",
            methods = "*")
public class PlayerPositionContract {

    private static final byte[] contractOwnerKey = new byte[] { 0x00 };

    /**
     * Метод развертывания контракта.
     *
     * @param data ScriptHash аккаунта владельца
     * @throws Exception ScriptHash не указан
     */
    @OnDeployment
    public static void deploy(Object data, boolean flag)
        throws Exception {
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
     * @throws Exception если размер serializableCords не равен 20( 5 int'ов) или hash подписавшего
     *                   транзакцию не равен playerHash
     */
    public static void putCords(Hash160 playerHash, byte[] serializableCords)
        throws Exception {
        if (serializableCords.length != 20) {
            throw new Exception("serializableCords array size must be 20");
        }
        if (Runtime.checkWitness(playerHash)) {
            throw new Exception("No authorization");
        }

        Storage.put(Storage.getStorageContext(), playerHash.toByteString(), serializableCords);
    }

    /**
     * Получает адрес владельца контракта из хранилища блокчейна.
     *
     * @return Хэш ScriptHash адреса владельца контракта.
     */
    private static Hash160 contractOwner() {
        return Storage.getHash160(Storage.getReadOnlyContext(), contractOwnerKey);
    }

    /**
     * Метод для получения координат игрока.
     *
     * @param playerHash hash игрока, чьи координаты мы хотим узнать
     * @return координаты игрока
     */
    public static byte[] getCords(Hash160 playerHash)
        throws Exception {

        return Storage.getByteArray(Storage.getStorageContext(), playerHash.toByteArray());
    }

    /**
     * Уничтожает контракт. Вызывается только владельцем контракта.
     *
     * @throws Exception если вызывающий метод не является владельцем контракта.
     */
    public static void destroy()
        throws Exception {
        if (Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
        }

        new ContractManagement().destroy();
    }

    /**
     * Обновляет контракт с новым файлом Nef и манифестом. Вызывается только владельцем контракта.
     *
     * @param nefFile  Байтовая строка (ByteString) нового файла Nef контракта.
     * @param manifest Манифест контракта в формате JSON, представленный в виде строки.
     * @param data     Дополнительные данные для обновления контракта (необязательно).
     * @throws Exception если вызывающий метод не является владельцем контракта.
     */
    public static void update(ByteString nefFile, String manifest, Object data)
        throws Exception {
        if (Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
        }

        if (data == null) {
            new ContractManagement().update(nefFile, manifest);
        } else {
            new ContractManagement().update(nefFile, manifest, data);
        }
    }

    /**
     * Обновляет контракт с новым файлом Nef и манифестом. Вызывается только владельцем контракта.
     *
     * @param nefFile  Байтовая строка (ByteString) нового файла Nef контракта.
     * @param manifest Манифест контракта в формате JSON, представленный в виде строки.
     * @throws Exception если вызывающий метод не является владельцем контракта.
     */
    public static void update(ByteString nefFile, String manifest)
        throws Exception {
        update(nefFile, manifest, null);
    }
}
