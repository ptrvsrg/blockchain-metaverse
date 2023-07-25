package ru.nsu.sberlab.contracts;

import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;

public class Main {

    private static final String MAP_HASH_CONF_NAME = "connection.hash160_map";
    private static final String STATE_HASH_CONF_NAME = "connection.hash160_state";

    public static void main(String[] args) {

        Account account = Account.fromWIF(args[0]);
        String httpURL = args[1];
        String name = args[2];

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

        System.out.printf("%s=%s\n", MAP_HASH_CONF_NAME, mapContractHash);
        System.out.printf("%s=%s\n", STATE_HASH_CONF_NAME, stateContractHash);
    }


}
