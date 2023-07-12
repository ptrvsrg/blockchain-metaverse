package blockchain.smartContract;


import blockchain.BlockInformation;

import blockchain.NodeInteraction;
import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoApplicationLog;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.protocol.core.stackitem.StackItem;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.transaction.Transaction;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.types.Hash256;
import io.neow3j.types.NeoVMStateType;
import io.neow3j.utils.Await;
import io.neow3j.wallet.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MapChangesSmartContractTest {

    private static final Account OWNER_ACCOUNT = Account.fromWIF("L1cUGcqEbqaZ8JHHRqQVbc4ZgFsVifWkiR9b7nQXG9SZqthEW7Uk");



    private static Hash160 contractHash = new Hash160("0x1b74ca38e6eff79fe2715797435b822d5be75cfb");

    private static NodeInteraction nodeInteraction = new NodeInteraction("http://localhost:50012", OWNER_ACCOUNT);



    @Test
    public void putGetTest() throws Throwable {
        BlockInformation blockInformation = new BlockInformation(1, 128, 999, 0, -1, 23);



//        contractHash = nodeInteraction.deployContract(MapChangesSmartContract.class.getCanonicalName());


        nodeInteraction.invokeFunctionInContract(contractHash, "putChanges", ContractParameter.byteArray(blockInformation.getBytesPresentation()));

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getAllChanges").getByteArray();

        BlockInformation appendedBlockInfo = new BlockInformation(Arrays.copyOfRange(result, result.length - 24, result.length));

        assertThat(appendedBlockInfo.toString()).isEqualTo(blockInformation.toString());

    }


    //надо поправить
//    @AfterAll
//    public static void destroyContract() throws Throwable {
//        nodeInteraction.invokeFunctionInContract(contractHash, "destroy");
//    }


}
