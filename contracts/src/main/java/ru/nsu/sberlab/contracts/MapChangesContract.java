package ru.nsu.sberlab.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Helper;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.contracts.ContractManagement;

/**
 * Класс MapChangesContract представляет собой контракт карты для хранения истории изменений.
 */
@DisplayName("${Name}")
@Permission(contract = "*",
            methods = "*")
public class MapChangesContract {

    public static final int BYTE_BLOCK_SIZE = 24;
    private static final byte[] allChangesListKey = new byte[] { 0x01 };
    private static final byte[] sizeOfAllChangesKey = new byte[] { 0x02 };
    private static final byte[] contractOwnerKey = new byte[] { 0x00 };

    /**
     * Метод для развертывания контракта.
     *
     * @param data Hash код аккаунта, который развертывает контракт
     */
    @OnDeployment
    public static void deploy(final Object data, boolean ignore)
        throws Exception {
        StorageContext ctx = Storage.getStorageContext();

        Storage.put(ctx, contractOwnerKey, (Hash160) data);

        byte[] emptyArray = new byte[0];
        Storage.put(ctx, allChangesListKey, emptyArray);

    }

    /**
     * Метод для добавления изменений в контракт.
     *
     * @param blockInformationByteRepresentation массив из сериализованного представления объектов
     *                                           BlockInformation
     * @throws Exception выбрасывается если размер входного массива не кратен размеру
     *                   BlockInformation
     */
    public static void putChanges(final byte[] blockInformationByteRepresentation)
        throws Exception {
        if (blockInformationByteRepresentation.length % BYTE_BLOCK_SIZE != 0) {
            throw new Exception("input array size must be multiple 24");
        }

        byte[] allChangesByteArray = Storage.getByteArray(Storage.getStorageContext(),
                                                          allChangesListKey);
        byte[] newAllChanges = new byte[allChangesByteArray.length +
                                        blockInformationByteRepresentation.length];
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
        byte[] lastLengthMinusNChanges = new byte[allChanges.length - BYTE_BLOCK_SIZE * N];

        Helper.memcpy(lastLengthMinusNChanges, 0, allChanges, N * BYTE_BLOCK_SIZE,
                      allChanges.length - BYTE_BLOCK_SIZE * N);

        return lastLengthMinusNChanges;
    }

    /**
     * Возвращает range изменений n штук начиная с i-того.
     *
     * @param i начальный индекс
     * @param n количество запрашиваемых изменений
     * @return n изменений, начиная с i-того
     */
    public static byte[] getRangeOfChanges(final int i, final int n) {

        byte[] allChanges = Storage.getByteArray(Storage.getStorageContext(), allChangesListKey);

        int actualSize = Math.min(allChanges.length / 24 - i, n);

        return Helper.range(allChanges, i * 24, actualSize * 24);
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
    public static void clear()
        throws Exception {
        if (Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
        }

        byte[] emptyArray = new byte[0];
        Storage.put(Storage.getStorageContext(), allChangesListKey, emptyArray);
    }

    public static void destroy()
        throws Exception {
        if (Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
        }

        new ContractManagement().destroy();
    }

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

    public static void update(ByteString nefFile, String manifest)
        throws Exception {
        update(nefFile, manifest, null);
    }
}
