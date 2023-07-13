package ru.nsu.sberlab.contracts.mapcontract;


import io.neow3j.transaction.exceptions.TransactionConfigurationException;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.nsu.sberlab.contracts.utils.BlockInfo;
import ru.nsu.sberlab.contracts.utils.NodeInteraction;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MapChangesSmartContractTest {

    private static Account OWNER_ACCOUNT;

    private static Hash160 contractHash;

    private static NodeInteraction nodeInteraction;

    private static final HashMap<String, String> confMap = new HashMap<>(3);

    @BeforeAll
    public static void readConfig() throws Exception {
        confMap.put("contractHash", "");
        confMap.put("accountWif", "");
        confMap.put("nodeURL", "");

        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(MapChangesSmartContractTest.class.getResourceAsStream("/map_contract/config.txt"))));
        String line = reader.readLine();
        for (int i = 0; i < confMap.size() && line != null; i++, line = reader.readLine()) {
            String[] lineSplit = line.split("=");
            if (lineSplit.length != 2 || !confMap.containsKey(lineSplit[0]))
                throw new Exception(format("wrong config file format. error in %d line", i + 1));
            confMap.put(lineSplit[0], lineSplit[1]);
        }
        reader.close();

        OWNER_ACCOUNT = Account.fromWIF(confMap.get("accountWif"));
        nodeInteraction = new NodeInteraction(confMap.get("nodeURL"), OWNER_ACCOUNT);
    }


    @Order (1)
    @Test
    public void deploy() throws Throwable {

        try {
            contractHash = nodeInteraction.deployContract(MapChangesSmartContract.class.getCanonicalName(), ContractParameter.hash160(OWNER_ACCOUNT.getScriptHash()));
        } catch (TransactionConfigurationException e) {
            if (e.getMessage().contains("Contract Already Exists: ")) {
                contractHash = new Hash160(e.getMessage().substring(e.getMessage().indexOf("Contract Already Exists: ") + "Contract Already Exists: ".length()));
                System.out.println("contract already deployed");
            } else {
                throw e;
            }
        }


        confMap.put("contractHash", contractHash.toString());
    }


    @Test
    @Order (2)
    public void putGetTest() throws Throwable {
        BlockInfo BlockInfo = new BlockInfo(1, 128, 999, 0, -1, 23);
        contractHash = new Hash160(confMap.get("contractHash"));


        nodeInteraction.invokeFunctionInContract(contractHash, "putChanges", ContractParameter.byteArray(BlockInfo.getBytesPresentation()));

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getAllChanges").getByteArray();

        BlockInfo appendedBlockInfo = new BlockInfo(Arrays.copyOfRange(result, result.length - 24, result.length));

        assertThat(appendedBlockInfo.toString()).isEqualTo(BlockInfo.toString());

    }


    @Test
    @Order (3)
    public void getLastNChangesTest() throws Throwable {
        int N = 5;
        byte[] newBytes = new byte[BlockInfo.BlockInfoByteSize * N];

        contractHash = new Hash160(confMap.get("contractHash"));

        for (int i = 0; i < newBytes.length; i++) {
            newBytes[i] = (byte) i;
        }
        nodeInteraction.invokeFunctionInContract(contractHash, "putChanges", ContractParameter.byteArray(newBytes));

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getChangesWithoutFirstN", ContractParameter.integer(1)).getByteArray();


        assertThat(result).containsExactly(newBytes);

    }


    @Test
    @Order (4)
    public void clearChangesHistoryTest() throws Throwable {
        contractHash = new Hash160(confMap.get("contractHash"));
        nodeInteraction.invokeFunctionInContract(contractHash, "clear");

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getAllChanges").getByteArray();

        assertThat(result.length).isEqualTo(0);

    }

    @AfterAll
    public static void updateConfig() throws IOException, URISyntaxException {

        final BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("src/test/resources/config.txt"));


        confMap.forEach((String key, String value) -> {
            try {
                bufferedWriter.write(format("%s=%s\n", key, value));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        bufferedWriter.flush();
        bufferedWriter.close();
    }

}
