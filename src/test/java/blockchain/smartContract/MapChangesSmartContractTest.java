package blockchain.smartContract;


import blockchain.BlockInformation;
import blockchain.NodeInteraction;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MapChangesSmartContractTest {

    private static final Account OWNER_ACCOUNT = Account.fromWIF("L1cUGcqEbqaZ8JHHRqQVbc4ZgFsVifWkiR9b7nQXG9SZqthEW7Uk");


    private static Hash160 contractHash = new Hash160("0x2a9b351971949cb5d05b71fef1d21c67cd71af8f");

    private static final NodeInteraction nodeInteraction = new NodeInteraction("http://localhost:50012", OWNER_ACCOUNT);


    @Order(1)
    @Test
    public void deploy() throws Throwable {

        contractHash = nodeInteraction.deployContract(MapChangesSmartContract.class.getCanonicalName(), ContractParameter.hash160(OWNER_ACCOUNT.getScriptHash()));

    }


    @Test
    @Order(2)
    public void putGetTest() throws Throwable {
        BlockInformation blockInformation = new BlockInformation(1, 128, 999, 0, -1, 23);


        nodeInteraction.invokeFunctionInContract(contractHash, "putChanges", ContractParameter.byteArray(blockInformation.getBytesPresentation()));

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getAllChanges").getByteArray();

        BlockInformation appendedBlockInfo = new BlockInformation(Arrays.copyOfRange(result, result.length - 24, result.length));

        assertThat(appendedBlockInfo.toString()).isEqualTo(blockInformation.toString());

    }


    @Test
    @Order(3)
    public void getLastNChangesTest() throws Throwable {
        int N = 5;
        byte[] newBytes = new byte[BlockInformation.BlockInformationByteSize * N];

        for (int i = 0; i < newBytes.length; i++) {
            newBytes[i] = (byte) i;
        }
        nodeInteraction.invokeFunctionInContract(contractHash, "putChanges", ContractParameter.byteArray(newBytes));

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getChangesWithoutFirstN", ContractParameter.integer(1)).getByteArray();


        assertThat(result).containsExactly(newBytes);

    }


    @Test
    @Order(4)
    public void clearChangesHistoryTest() throws Throwable {
        nodeInteraction.invokeFunctionInContract(contractHash, "clear");

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getAllChanges").getByteArray();

        assertThat(result.length).isEqualTo(0);

    }


}
