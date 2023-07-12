package blockchain.smartContract;


import blockchain.BlockInformation;

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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MapChangesSmartContractTest {

    private static final String PUT_CHANGES = "putChanges";

    private static final String GET_PART_OF_CHANGES = "getChangesWithoutFirstN";

    private static final String GET_ALL_CHANGES = "getAllChanges";

    private static final Account OWNER_ACCOUNT = Account.fromWIF("L1cUGcqEbqaZ8JHHRqQVbc4ZgFsVifWkiR9b7nQXG9SZqthEW7Uk");


    private static Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:50012"));

    private static AccountSigner signerOwner = AccountSigner.none(OWNER_ACCOUNT);
    private static Hash160 contractHash = new Hash160("0xcd64e1e94e927b799c1de0bf0cb6703ebd14b336");


//    @BeforeAll
//    public static void DeployContract() throws Throwable {
//
//        CompilationUnit res = new Compiler().compile(MapChangesSmartContract.class.getCanonicalName());
//
//
//        Transaction transaction = new ContractManagement(neow3j)
//                .deploy(res.getNefFile(), res.getManifest(), ContractParameter.hash160(OWNER_ACCOUNT))
//                .signers(signerOwner)
//                .sign();
//        NeoSendRawTransaction response = transaction.send();
//
//        if (response.hasError()) {
//            throw new Exception("Sent transaction resulted in an error: " + response.getError().getMessage());
//        }
//
//        Hash256 txHash = response.getResult().getHash();
//        System.out.printf("Deployment transaction hash: '%s'\n", txHash);
//        Await.waitUntilTransactionIsExecuted(txHash, neow3j);
//
//        NeoApplicationLog log = neow3j.getApplicationLog(txHash).send().getApplicationLog();
//        if (log.getExecutions().get(0).getState().equals(NeoVMStateType.FAULT)) {
//
//
//            throw new Exception(format("Failed to deploy contract. NeoVM error message: %s",
//                    log.getExecutions().get(0).getException()));
//        }
//
//        contractHash = SmartContract.calcContractHash(
//                signerOwner.getScriptHash(),
//                res.getNefFile().getCheckSumAsInteger(),
//                res.getManifest().getName()
//        );
//        System.out.printf("Contract hash: '%s'\n", contractHash);
//
//
//    }

    @Test
    public void putGetTest() throws Throwable {
        Transaction transaction = new SmartContract(contractHash, neow3j)
                .invokeFunction(PUT_CHANGES, ContractParameter.integer(1), ContractParameter.integer(1), ContractParameter.integer(2), ContractParameter.integer(3), ContractParameter.integer(4), ContractParameter.integer(5))
                .signers(signerOwner)
                .sign();
        NeoSendRawTransaction response = transaction.send();



//        System.out.println(response.getError().getMessage());
        assertThat(response.hasError()).isFalse();

        Hash256 txHash = response.getResult().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);


        NeoApplicationLog log = neow3j.getApplicationLog(txHash).send().getApplicationLog();
        assertThat(log.getExecutions().get(0).getState().equals(NeoVMStateType.FAULT)).isFalse();

        transaction = new SmartContract(contractHash, neow3j)
                .invokeFunction(GET_ALL_CHANGES)
                .signers(signerOwner)
                .sign();
        response = transaction.send();


        assertThat(response.hasError()).isFalse();

        txHash = response.getResult().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);


        log = neow3j.getApplicationLog(txHash).send().getApplicationLog();
        assertThat(log.getExecutions().get(0).getState().equals(NeoVMStateType.FAULT)).isFalse();


        NeoApplicationLog.Execution execution = log.getExecutions().get(0);
        if (execution.getState().equals(NeoVMStateType.FAULT)) {
            throw new Exception("Invocation failed");
        }
        List<StackItem> stack = execution.getStack();

        byte[] returnValue = stack.get(0).getByteArray();

        BlockInformation blockInformation = new BlockInformation(Arrays.copyOfRange(returnValue, returnValue.length - 24, returnValue.length) );

        assertThat(blockInformation.getChunkX()).isEqualTo(1);
        assertThat(blockInformation.getChunkY()).isEqualTo(1);
        assertThat(blockInformation.getX()).isEqualTo(2);
        assertThat(blockInformation.getY()).isEqualTo(3);
        assertThat(blockInformation.getZ()).isEqualTo(4);
        assertThat(blockInformation.getBlockId()).isEqualTo(5);

    }


//    @AfterAll
//    public static void destroyContract() throws Throwable {
//
//        Transaction transaction = new SmartContract(contractHash, neow3j).invokeFunction("destroy").signers(signerOwner).sign();
//
//        NeoSendRawTransaction response = transaction.send();
//
//        if (response.hasError()) {
//            throw new Exception("Sent transaction resulted in an error: " + response.getError().getMessage());
//        }
//
//        Hash256 txHash = response.getResult().getHash();
//        System.out.printf("Deployment transaction hash: '%s'\n", txHash);
//        Await.waitUntilTransactionIsExecuted(txHash, neow3j);
//
//        NeoApplicationLog log = neow3j.getApplicationLog(txHash).send().getApplicationLog();
//        if (log.getExecutions().get(0).getState().equals(NeoVMStateType.FAULT)) {
//
//            throw new Exception(format("Failed to destroy contract. NeoVM error message: %s",
//                    log.getExecutions().get(0).getException()));
//        }
//
//    }


}
