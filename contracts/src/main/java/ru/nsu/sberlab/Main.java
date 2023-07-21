package ru.nsu.sberlab;

import io.neow3j.types.ContractParameter;
import io.neow3j.wallet.Account;
import ru.nsu.sberlab.contracts.CompilationDeploying;

public class Main {

    private static final String ownerWifEnvVar = "OWNER_WIF";
    private static final String nodeURLEnvVar = "NODE_URL";

    private static final String nameEnvVar = "NAME";

    public static void main(String[] args) {

        Account account = Account.fromWIF(System.getenv(ownerWifEnvVar));
        String httpURL = System.getenv(nodeURLEnvVar);
        String name = System.getenv(nameEnvVar);

        CompilationDeploying compilationDeploying = new CompilationDeploying(httpURL, account);

        try {

            System.out.printf("map contract Hash: %s%n", compilationDeploying.deployMapContract(ContractParameter.hash160(account.getScriptHash()), name).toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {

            System.out.printf("state contract Hash: %s%n", compilationDeploying.deployStateContract(ContractParameter.hash160(account.getScriptHash()), name).toString());

        } catch (Throwable e) {
            e.printStackTrace();
        }


    }
}