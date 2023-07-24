package ru.nsu.sberlab;

import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import ru.nsu.sberlab.contracts.CompilationDeploying;
import ru.nsu.sberlab.contracts.MapContractAlreadyExist;
import ru.nsu.sberlab.contracts.StateContractAlreadyExist;

import java.io.*;

public class Main {

    private static final String ownerWifEnvVar = "OWNER_WIF";
    private static final String nodeURLEnvVar = "NODE_URL";

    private static final String CONFIG_FILE_PATH = "start-menu/src/main/resources/config/connection.properties";

    private static final String MAP_HASH_CONF_NAME = "connection.hash160_map";
    private static final String STATE_HASH_CONF_NAME = "connection.hash160_state"

    private static final String nameEnvVar = "NAME";

    public static void main(String[] args) {

        Account account = Account.fromWIF(System.getenv(ownerWifEnvVar));
        String httpURL = System.getenv(nodeURLEnvVar);
        String name = System.getenv(nameEnvVar);

        CompilationDeploying compilationDeploying = new CompilationDeploying(httpURL, account);

        Hash160 mapContractHash;
        Hash160 stateContractHash;

        try {
            mapContractHash = compilationDeploying.deployMapContract(ContractParameter.hash160(account.getScriptHash()), name);
        } catch (MapContractAlreadyExist e) {
            mapContractHash = e.getMapContractExistHash();
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }

        try {
            stateContractHash = compilationDeploying.deployStateContract(ContractParameter.hash160(account.getScriptHash()), name);
        } catch (StateContractAlreadyExist e) {
            stateContractHash = e.getStateContractExistHash();
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }



        try (FileWriter fileWriter = new FileWriter(CONFIG_FILE_PATH)) {
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.printf("%s=%s\n", MAP_HASH_CONF_NAME, mapContractHash);
            printWriter.printf("%s=%s\n",STATE_HASH_CONF_NAME, stateContractHash);
        }catch (Exception e) {
            e.printStackTrace();
        }


    }


}
