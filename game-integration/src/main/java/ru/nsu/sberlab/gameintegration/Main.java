package ru.nsu.sberlab.gameintegration;

import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import ru.nsu.sberlab.blockchain_interaction.MapInteraction;

public class Main {
    public static void main(String[] args) throws Throwable {
        var map = new MapInteraction("http://45.9.24.41:20032", Account.fromWIF("L2btC2CKdpBE32hz4qTeLjYsP9dYKWNzYQH4Bmkt8BzRSviNZW1X"),
                new Hash160("ad887e7a8c228f9bd94dc888190433dc71c81fb8"), new Hash160("c49920e21449a3fb1cd19685644093c034bb576e"));

        var res = map.getRangeChanges(0, 1000);
        System.out.println(res.size());
    }
}
