package ru.nsu.sberlab.blockchain.contracts;


import io.neow3j.transaction.exceptions.TransactionConfigurationException;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import org.junit.jupiter.api.*;
import ru.nsu.sberlab.blockchain.blockchain_interaction.utils.BlockInfo;
import ru.nsu.sberlab.blockchain.blockchain_interaction.utils.NodeInteraction;

import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapChangesContractTest {
    private static final String ownerWifEnvVar = "OWNER_WIF";
    private static final String nodeURLEnvVar = "NODE_URL";
    private static final HashMap<String, String> confMap = new HashMap<>(3);
    private static Account ownerAccount;
    private static Hash160 contractHash;
    private static NodeInteraction nodeInteraction;

    @BeforeAll
    public static void readConfig() {
        ownerAccount = Account.fromWIF(System.getenv(ownerWifEnvVar));
        nodeInteraction = new NodeInteraction(System.getenv(nodeURLEnvVar), ownerAccount);

    }


    @Test
    @Order(0)
    public void deploy() throws Throwable {
        HashMap<String, String> replaceMap = new HashMap<>(1);
        replaceMap.put("Name", "test2");

        try {
            contractHash = nodeInteraction.deployContract(MapChangesContract.class.getCanonicalName(),
                    ContractParameter.hash160(ownerAccount.getScriptHash()), replaceMap);
        } catch (TransactionConfigurationException e) {
            if (e.getMessage().contains("Contract Already Exists: ")) {
                contractHash = new Hash160(e.getMessage().substring(e.getMessage().indexOf("Contract Already Exists: ")
                        + "Contract Already Exists: ".length()));
                System.out.println("contract already deployed");
            } else {
                throw e;
            }
        }


    }


    @Order(1)
    @Test
    public void putGetTest() throws Throwable {
        BlockInfo BlockInfo = new BlockInfo(1, 128, 999, 0, -1, 23);


        nodeInteraction.invokeFunctionInContract(contractHash, "putChanges", ContractParameter.byteArray(BlockInfo.serialize()));

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getAllChanges").getByteArray();

        BlockInfo appendedBlockInfo = new BlockInfo(Arrays.copyOfRange(result, result.length - 24, result.length));

        assertThat(appendedBlockInfo).isEqualTo(BlockInfo);

    }


    @Order(3)
    @Test
    public void clearChangesHistoryTest() throws Throwable {
        nodeInteraction.invokeFunctionInContract(contractHash, "clear");

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getAllChanges").getByteArray();
        assertThat(result.length).isEqualTo(0);

    }

    @Order(2)
    @Test
    public void getLastNChangesTest() throws Throwable {
        int N = 5;
        byte[] newBytes = new byte[BlockInfo.BlockInfoByteSize * (N + 1)];

        for (int i = 24; i < newBytes.length; i++) {
            newBytes[i] = (byte) i;
        }
        nodeInteraction.invokeFunctionInContract(contractHash, "putChanges", ContractParameter.byteArray(newBytes));

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getChangesWithoutFirstN", ContractParameter.integer(1)).getByteArray();


        assertThat(result).containsExactly(newBytes);

    }

}
