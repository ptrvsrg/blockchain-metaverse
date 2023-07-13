package ru.nsu.sberlab.smartcontract;

import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.nsu.sberlab.NodeInteraction;
import ru.nsu.sberlab.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PlayerPositionTest {

    private static Account OWNER_ACCOUNT;


    private static Hash160 contractHash;

    private static NodeInteraction nodeInteraction;

    private static final HashMap<String, String> confMap = new HashMap<>(3);

    @BeforeAll
    public static void readConfig() throws Exception {
        confMap.put("contractHash", "");
        confMap.put("accountWif", "");
        confMap.put("nodeURL", "");

        InputStream file = Files.newInputStream(Paths.get("config.txt"));
        InputStreamReader streamReader = new InputStreamReader(file);
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        for (int i = 0; i < confMap.size(); i++) {
            line = reader.readLine();
            String[] lineSplit = line.split("=");
            if (lineSplit.length != 2 || !confMap.containsKey(lineSplit[0]))
                throw new Exception("wrong config file format");
            confMap.put(lineSplit[0], lineSplit[1]);
        }
        reader.close();
        streamReader.close();
        file.close();

        contractHash = new Hash160(confMap.get("contractHash"));
        OWNER_ACCOUNT = Account.fromWIF(confMap.get("accountWif"));
        nodeInteraction = new NodeInteraction(confMap.get("nodeURL"), OWNER_ACCOUNT);

    }

    @Order(1)
    @Test
    public void deploy() throws Throwable {

        contractHash = nodeInteraction.deployContract(PlayerPositionContract.class.getCanonicalName(), ContractParameter.hash160(OWNER_ACCOUNT.getScriptHash()));

        confMap.put("contractHash", contractHash.toString());
    }


    @Test
    @Order(2)
    public void putGetTest() throws Throwable {
        int[] cords = new int[3];
        cords[0] = 1;
        cords[1] = 2;
        cords[2] = 3;


        nodeInteraction.invokeFunctionInContract(contractHash, "putCords", ContractParameter.byteArray(Utils.intArrayToByteArray(cords)));

        byte[] result = nodeInteraction.invokeFunctionInContract(contractHash, "getCords").getByteArray();



        assertThat(Utils.ByteArrayToIntArray(result)).containsExactly(cords);
    }


    @AfterAll
    public static void writeConfigToConfigFile() throws IOException {
        final OutputStream file = Files.newOutputStream(Paths.get("config.txt"));
        final OutputStreamWriter streamWriter = new OutputStreamWriter(file);
        final BufferedWriter writer = new BufferedWriter(streamWriter);

        confMap.forEach((String key, String value) -> {
            try {
                writer.write(format("%s=%s\n", key, value));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        writer.close();
        streamWriter.close();
        file.close();
    }



}
