package ru.nsu.sberlab.contracts.statecontract;

import io.neow3j.transaction.exceptions.TransactionConfigurationException;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.nsu.sberlab.blockchain_interaction.utils.NodeInteraction;
import ru.nsu.sberlab.blockchain_interaction.utils.Utils;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerPositionTest {

    private static Account ownerAccount;

    private static Hash160 contractHash;

    private static NodeInteraction nodeInteraction;

    private static final String ownerWifEnvVar = "OWNER_WIF";
    private static final String nodeURLEnvVar = "NODE_URL";

    @BeforeAll
    public static void readConfig() {
        ownerAccount = Account.fromWIF(System.getenv(ownerWifEnvVar));
        nodeInteraction = new NodeInteraction(System.getenv(nodeURLEnvVar), ownerAccount);

    }

    @Order(1)
    @Test
    public void deploy() throws Throwable {
        HashMap<String, String> replaceMap = new HashMap<>(1);
        replaceMap.put("Name", "test");

        try {
            contractHash = nodeInteraction.deployContract(PlayerPositionContract.class.getCanonicalName(),
                    ContractParameter.hash160(ownerAccount.getScriptHash()), replaceMap);
        } catch (TransactionConfigurationException e) {
            if (e.getMessage().contains("Contract Already Exists: ")) {
                contractHash = new Hash160(e.getMessage().substring(e.getMessage().indexOf("Contract Already Exists: ")
                        + "Contract Already Exists: ".length()).trim());
                System.out.println("contract already deployed. Hash: " + contractHash);
            } else {
                throw e;
            }
        }
    }

    @Test
    @Order(2)
    public void putGetTest() throws Throwable {
        int[] cords = new int[5];

        for (int i = 0; i < cords.length; i++) {
            cords[i] = i + 1;
        }



        nodeInteraction.invokeFunctionInContract(contractHash, "putCords",
                ContractParameter.byteArray(Utils.intArrayToByteArray(cords)));

        int[] result = Utils.byteArrayToIntArray(nodeInteraction.invokeFunctionInContract(contractHash,
                "getCords").getByteArray());

        assertThat(result).containsExactly(cords);
    }
}
