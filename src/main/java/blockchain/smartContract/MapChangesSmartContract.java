package blockchain.smartContract;

import blockchain.BlockInformation;
import io.neow3j.devpack.*;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.annotations.*;
import io.neow3j.devpack.constants.NativeContract;
import io.neow3j.devpack.contracts.ContractManagement;


@DisplayName("MapChangesContract")
@ManifestExtra(key = "author", value = "Your Name")
public class MapChangesSmartContract {

    private static final byte[] allChangesListKey = new byte[]{0x01};
    private static final byte[] contractOwnerKey = new byte[]{0x00};


    @OnDeployment
    public static void deploy(Object data, boolean _) throws Exception {
        StorageContext ctx = Storage.getStorageContext();


        if (!(data instanceof Hash160)) {
            throw new Exception("can't cast data to string");
        }

        Storage.put(ctx, contractOwnerKey, (Hash160)data);


        byte[] emptyArray = new byte[0];
        Storage.put(ctx, allChangesListKey, emptyArray);

    }
    public static void putChanges(byte[] blockInformationByteRepresentation) {
        byte[] allChangesByteArray = Storage.getByteArray(Storage.getStorageContext(), allChangesListKey);
        byte[] newAllChanges = new byte[allChangesByteArray.length + BlockInformation.BlockInformationByteSize];
        Helper.memcpy(newAllChanges, 0, allChangesByteArray, 0, allChangesByteArray.length);
        Helper.memcpy(newAllChanges, allChangesByteArray.length, blockInformationByteRepresentation, 0, BlockInformation.BlockInformationByteSize);

        Storage.put(Storage.getStorageContext(), allChangesListKey, newAllChanges);
    }


    public static byte[] getAllChanges() {
        return Storage.getByteArray(Storage.getStorageContext(), allChangesListKey);
    }



    public static byte[] getChangesWithoutFirstN(int N) {
        byte[] allChanges = Storage.getByteArray(Storage.getStorageContext(), allChangesListKey);
        byte[] lastLengthMinusNChanges = new byte[allChanges.length - BlockInformation.BlockInformationByteSize * N];

        Helper.memcpy(lastLengthMinusNChanges, 0, allChanges, N * BlockInformation.BlockInformationByteSize, allChanges.length - BlockInformation.BlockInformationByteSize * N);

        return lastLengthMinusNChanges;
    }


    public static Hash160 contractOwner() {
        return Storage.getHash160(Storage.getReadOnlyContext(), contractOwnerKey);
    }



    public static void destroy() throws Exception {
//        if (!Runtime.checkWitness(contractOwner())) {
//            throw new Exception("No authorization");
//        }
        (new ContractManagement()).destroy();
    }


}