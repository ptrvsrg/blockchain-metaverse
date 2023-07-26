package ru.nsu.sberlab.contracts;

import static java.lang.String.format;

import io.neow3j.compiler.CompilationUnit;
import io.neow3j.compiler.Compiler;
import io.neow3j.contract.ContractManagement;
import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoApplicationLog;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.transaction.Transaction;
import io.neow3j.transaction.exceptions.TransactionConfigurationException;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.types.Hash256;
import io.neow3j.types.NeoVMStateType;
import io.neow3j.utils.Await;
import io.neow3j.wallet.Account;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для развертывания контрактов на блокчейне с использованием компиляции и деплоя.
 */
public class CompilationDeploying {

    private final AccountSigner signerOwner;
    private final Neow3j node;


    /**
     * Конструктор CompilationDeploying.
     *
     * @param httpUrl http-адрес ноды
     * @param account аккаунт для подписывания транзакций
     */
    public CompilationDeploying(String httpUrl, Account account) {
        node = Neow3j.build(new HttpService(httpUrl));
        signerOwner = AccountSigner.none(account);
    }

    /**
     * Метод развертывает контракт MapChangesContract
     *
     * @param parameter параметр для передачи при развертывании контракта
     * @param name      название контракта
     * @return возвращает ScriptHash развернутого контракта
     * @throws MapContractAlreadyExist если контракт с таким именем уже существует
     * @throws Throwable               если происходит ошибка при развертывании
     */
    public Hash160 deployMapContract(ContractParameter parameter, String name)
        throws Throwable {
        HashMap<String, String> replaceMap = new HashMap<>(1);
        replaceMap.put("Name", name);

        Hash160 mapContractHash;
        try {
            mapContractHash = deployContract(MapChangesContract.class.getCanonicalName(), parameter,
                                             replaceMap);
        } catch (TransactionConfigurationException e) {
            if (e.getMessage()
                 .contains("Contract Already Exists: ")) {
                mapContractHash = new Hash160(e.getMessage()
                                               .substring(e.getMessage()
                                                           .indexOf("Contract Already Exists: ") +
                                                          "Contract Already Exists: ".length()));
                throw new MapContractAlreadyExist(mapContractHash, name);
            } else {
                throw new Exception("unable to create map contract", e);
            }
        } catch (Throwable e) {
            throw new Exception("unable to create map contract", e);
        }

        return mapContractHash;
    }


    /**
     * Метод развертывает контракт PlayerPositionContract
     *
     * @param parameter параметр для передачи при развертывании контракта
     * @param name      название контракта
     * @return возвращает ScriptHash развернутого контракта
     * @throws Throwable если происходит ошибка при развертывании
     */
    public Hash160 deployStateContract(ContractParameter parameter, String name)
        throws Throwable {
        HashMap<String, String> replaceMap = new HashMap<>(1);
        replaceMap.put("Name", name);

        Hash160 stateContractHash;
        try {
            stateContractHash = deployContract(PlayerPositionContract.class.getCanonicalName(),
                                               parameter, replaceMap);
        } catch (TransactionConfigurationException e) {
            if (e.getMessage()
                 .contains("Contract Already Exists: ")) {
                stateContractHash = new Hash160(e.getMessage()
                                                 .substring(e.getMessage()
                                                             .indexOf("Contract Already Exists: ") +
                                                            "Contract Already Exists: ".length()));
                throw new StateContractAlreadyExist(stateContractHash, name);
            } else {
                throw new Exception("unable to create map contract", e);
            }
        } catch (Throwable e) {
            throw new Exception("unable to create map contract", e);
        }

        return stateContractHash;
    }

    /**
     * Метод для развертывания контракта.
     *
     * @param contractClassName каноническое имя класса(Class.getCanonicalName())
     * @param parameter         параметр для передачи при развертывании контракта
     * @param replaceMap        параметр для передачи замен для placeholders строк
     * @return возвращает ScriptHash задиплоенного контракта
     * @throws Throwable если происходит ошибка при развертывании
     */
    private Hash160 deployContract(String contractClassName, ContractParameter parameter,
                                   Map<String, String> replaceMap)
        throws Throwable {
        CompilationUnit res;

        if (replaceMap == null) {
            res = new Compiler().compile(contractClassName);
        } else {
            res = new Compiler().compile(contractClassName, replaceMap);
        }

        Transaction transaction = new ContractManagement(node).deploy(res.getNefFile(),
                                                                      res.getManifest(), parameter)
                                                              .signers(signerOwner)
                                                              .sign();
        NeoSendRawTransaction response = transaction.send();

        if (response.hasError()) {
            throw new Exception("Sent transaction resulted in an error: " + response.getError()
                                                                                    .getMessage());
        }

        Hash256 txHash = response.getResult()
                                 .getHash();
        System.out.printf("Deployment transaction hash: '%s'\n", txHash);
        Await.waitUntilTransactionIsExecuted(txHash, node);

        NeoApplicationLog log = node.getApplicationLog(txHash)
                                    .send()
                                    .getApplicationLog();
        if (log.getExecutions()
               .get(0)
               .getState()
               .equals(NeoVMStateType.FAULT)) {

            throw new Exception(format("Failed to deploy contract. NeoVM error message: %s",
                                       log.getExecutions()
                                          .get(0)
                                          .getException()));
        }

        return SmartContract.calcContractHash(signerOwner.getScriptHash(), res.getNefFile()
                                                                              .getCheckSumAsInteger(),
                                              res.getManifest()
                                                 .getName());
    }

}
