package ru.nsu.sberlab.contracts.mapcontract;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.Helper;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.Permission;
import ru.nsu.sberlab.contracts.utils.BlockInfo;


/**
 * Класс MapChangesContract представляет собой контракт карты
 * для хранения истории изменений.
 */
@DisplayName("MapChangesContract")
@ManifestExtra(key = "author", value = "Your Name")
@Permission(contract = "*", methods = "*")
public class MapChangesContract {

    private static final byte[] allChangesListKey = new byte[]{0x01};
    private static final byte[] contractOwnerKey = new byte[]{0x00};

    public static final int BYTE_BLOCK_SIZE = 24;

    /**
     * Метод для развертывания контракта.
     *
     * @param data Hash код аккаунта, который развертывает контракт
     */
    @OnDeployment
    public static void deploy(final Object data, boolean _) throws Exception {
        StorageContext ctx = Storage.getStorageContext();

        Storage.put(ctx, contractOwnerKey, (Hash160) data);

        byte[] emptyArray = new byte[0];
        Storage.put(ctx, allChangesListKey, emptyArray);

    }

    /**
     * Метод для добавления изменений в контракт.
     *
     * @param blockInformationByteRepresentation массив из сериализованного представления объектов BlockInformation
     * @throws Exception выбрасывается если размер входного массива не кратен размеру BlockInformation
     */
    public static void putChanges(final byte[] blockInformationByteRepresentation) throws Exception {
        if (blockInformationByteRepresentation.length % BYTE_BLOCK_SIZE != 0) {
            throw new Exception("input array size must be multiple 24");
        }

        byte[] allChangesByteArray = Storage.getByteArray(Storage.getStorageContext(), allChangesListKey);
        byte[] newAllChanges = new byte[allChangesByteArray.length + blockInformationByteRepresentation.length];
        Helper.memcpy(newAllChanges, 0, allChangesByteArray, 0, allChangesByteArray.length);
        Helper.memcpy(newAllChanges, allChangesByteArray.length, blockInformationByteRepresentation,
                0, blockInformationByteRepresentation.length);

        Storage.put(Storage.getStorageContext(), allChangesListKey, newAllChanges);
    }


    /**
     * Метод для получения всех изменений на карте.
     *
     * @return возвращает все изменения в сериализованном виде
     */
    public static byte[] getAllChanges() {
        return Storage.getByteArray(Storage.getStorageContext(), allChangesListKey);
    }


    /**
     * Метод для получения всех изменений без первых N штук.
     *
     * @param N колличество первых N изменений которые не надо возвращать
     * @return все изменения без первых N штук
     */
    public static byte[] getChangesWithoutFirstN(final int N) {
        byte[] allChanges = Storage.getByteArray(Storage.getStorageContext(), allChangesListKey);
        byte[] lastLengthMinusNChanges = new byte[allChanges.length - BlockInfo.BlockInfoByteSize * N];

        Helper.memcpy(lastLengthMinusNChanges, 0, allChanges, N * BlockInfo.BlockInfoByteSize,
                allChanges.length - BlockInfo.BlockInfoByteSize * N);

        return lastLengthMinusNChanges;
    }

    /**
     * Метод для получения владельца контракта.
     *
     * @return Возвращает хэш-код владельца контракта
     */
    public static Hash160 contractOwner() {
        return Storage.getHash160(Storage.getReadOnlyContext(), contractOwnerKey);
    }


    /**
     * Метод для полной очистки истории изменений.
     *
     * @throws Exception выбрасывается если человек вызвавший контракт не является его владельцем
     */
    public static void clear() throws Exception {
        if (Runtime.getCallingScriptHash().equals(contractOwner())) {
            throw new Exception("No authorization");
        }

        byte[] emptyArray = new byte[0];
        Storage.put(Storage.getStorageContext(), allChangesListKey, emptyArray);
    }
}
