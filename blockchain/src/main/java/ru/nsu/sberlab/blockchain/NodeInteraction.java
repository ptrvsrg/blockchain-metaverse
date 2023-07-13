package ru.nsu.sberlab.blockchain;

import io.neow3j.compiler.CompilationUnit;
import io.neow3j.compiler.Compiler;
import io.neow3j.contract.ContractManagement;
import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoApplicationLog;
import io.neow3j.protocol.core.response.NeoGetApplicationLog;
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

import static java.lang.String.format;


/**
 * Класс NodeInteraction предоставляет удобное взаимодействие с нодой.
 * Используется для развертывания контрактов, вызова функций в контрактах и других операций с блокчейном.
 */
public class NodeInteraction {
    private Neow3j node;

    private Account account;

    private final AccountSigner signerOwner;


    /**
     * Конструктор NodeInteraction.
     *
     * @param httpUrl http-адрес ноды
     * @param account аккаунт для подписывания транзакций
     */
    public NodeInteraction(String httpUrl, Account account) {
        node = Neow3j.build(new HttpService(httpUrl));
        this.account = account;
        signerOwner = AccountSigner.none(account);
    }


    /**
     *  Метод для развертывания контракта.
     *
     * @param contractClassName каноническое имя класса(Class.getCanonicalName())
     * @param parameter параметр для передачи при развертывании контракта
     * @return возвращает ScriptHash задиплоенного контракта
     * @throws Throwable если происходит ошибка при  развертывании
     */
    public Hash160 deployContract(String contractClassName, ContractParameter parameter) throws Throwable {
        CompilationUnit res = new Compiler().compile(contractClassName);


        Transaction transaction = new ContractManagement(node)
                .deploy(res.getNefFile(), res.getManifest(), parameter)
                .signers(signerOwner)
                .sign();
        NeoSendRawTransaction response = transaction.send();

        if (response.hasError()) {
            throw new Exception("Sent transaction resulted in an error: " + response.getError().getMessage());
        }

        Hash256 txHash = response.getResult().getHash();
        System.out.printf("Deployment transaction hash: '%s'\n", txHash);
        Await.waitUntilTransactionIsExecuted(txHash, node);

        NeoApplicationLog log = node.getApplicationLog(txHash).send().getApplicationLog();
        if (log.getExecutions().get(0).getState().equals(NeoVMStateType.FAULT)) {


            throw new Exception(format("Failed to deploy contract. NeoVM error message: %s",
                    log.getExecutions().get(0).getException()));
        }

        return SmartContract.calcContractHash(
                signerOwner.getScriptHash(),
                res.getNefFile().getCheckSumAsInteger(),
                res.getManifest().getName()
        );
    }


    /**
     * Метод для вызова функции в контракте.
     *
     * @param contactHash хэш контракта у которого мы собираемся вызвать функцию
     * @param function имя функции которую мы собираемся вызвать
     * @param params параметры для передачи в функцию
     * @return то что вернула функция
     * @throws Throwable если происходит ошибка при вызове функции в контракте
     */
    public StackItem invokeFunctionInContract(Hash160 contactHash, String function, ContractParameter... params) throws Throwable {

        Transaction transaction = new SmartContract(contactHash, node)
                .invokeFunction(function, params)
                .signers(signerOwner)
                .sign();
        NeoSendRawTransaction response = transaction.send();


        if (response.hasError()) {
            throw new Exception(format("Can't invoke function: \"%s\"  on contract: \"%s\". Cause: %s", function,
                    contactHash,
                    response.getError().getMessage()));
        }

        Hash256 txHash = response.getResult().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, node);


        NeoGetApplicationLog response1 = node.getApplicationLog(txHash).send();
        if (response1.hasError()) {
            throw new Exception("Error fetching transaction's app log: " + response.getError().getMessage());
        }
        // Get the first execution. Usually there is only one execution.
        NeoApplicationLog.Execution execution = response1.getApplicationLog().getExecutions().get(0);
        // Check if the execution ended in a NeoVM state FAULT.
        if (execution.getState().equals(NeoVMStateType.FAULT)) {
            throw new Exception("Invocation failed");
        }
        // Get the result stack.
        java.util.List<StackItem> stack = execution.getStack();
        return stack.get(0);
    }


    public Neow3j getNode() {
        return node;
    }

    public void setNode(Neow3j node) {
        this.node = node;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
