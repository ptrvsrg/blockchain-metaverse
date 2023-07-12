package blockchain.smartContract;

import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.*;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.Permission;

import static blockchain.BlockInformation.BlockInformationByteSize;


@DisplayName("MapChangesContract")
@ManifestExtra(key = "author", value = "Your Name")
@Permission(contract = "*", methods = "*")
public class MapChangesSmartContract {

    private static final byte[] allChangesListKey = new byte[]{0x01};
    private static final byte[] contractOwnerKey = new byte[]{0x00};


    /**
     * @param data Hash код аккаунта, который развертывает контракт
     */
    @OnDeployment
    public static void deploy(Object data, boolean _) throws Exception {
        StorageContext ctx = Storage.getStorageContext();

        Storage.put(ctx, contractOwnerKey, (Hash160) data);

        byte[] emptyArray = new byte[0];
        Storage.put(ctx, allChangesListKey, emptyArray);

    }

    /**
     * @param blockInformationByteRepresentation массив из сериализованного представления объектов BlockInformation
     * @throws Exception выбрасывается если размер входного массива не кратен размеру BlockInformation
     */
    public static void putChanges(byte[] blockInformationByteRepresentation) throws Exception {
        if (blockInformationByteRepresentation.length % 24 != 0)
            throw new Exception("input array size must be multiple 24");

        byte[] allChangesByteArray = Storage.getByteArray(Storage.getStorageContext(), allChangesListKey);
        byte[] newAllChanges = new byte[allChangesByteArray.length + blockInformationByteRepresentation.length];
        Helper.memcpy(newAllChanges, 0, allChangesByteArray, 0, allChangesByteArray.length);
        Helper.memcpy(newAllChanges, allChangesByteArray.length, blockInformationByteRepresentation, 0, blockInformationByteRepresentation.length);

        Storage.put(Storage.getStorageContext(), allChangesListKey, newAllChanges);
    }


    /**
     * @return возвращает все изменения в сериализованном виде
     */
    public static byte[] getAllChanges() {
        return Storage.getByteArray(Storage.getStorageContext(), allChangesListKey);
    }


    /**
     * @param N колличество первых N изменений которые не надо возвращать
     * @return все изменения без первых N штук
     */
    public static byte[] getChangesWithoutFirstN(int N) {
        byte[] allChanges = Storage.getByteArray(Storage.getStorageContext(), allChangesListKey);
        byte[] lastLengthMinusNChanges = new byte[allChanges.length - BlockInformationByteSize * N];

        Helper.memcpy(lastLengthMinusNChanges, 0, allChanges, N * BlockInformationByteSize, allChanges.length - BlockInformationByteSize * N);

        return lastLengthMinusNChanges;
    }


    public static Hash160 contractOwner() {
        return Storage.getHash160(Storage.getReadOnlyContext(), contractOwnerKey);
    }


    /**
     * функция полностью очищает историю изменений
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