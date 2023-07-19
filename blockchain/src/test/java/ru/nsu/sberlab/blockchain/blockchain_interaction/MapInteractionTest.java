package ru.nsu.sberlab.blockchain.blockchain_interaction;

import io.neow3j.wallet.Account;
import org.junit.jupiter.api.*;
import ru.nsu.sberlab.blockchain.blockchain_interaction.exception.BlockChainException;
import ru.nsu.sberlab.blockchain.blockchain_interaction.exception.MapAlreadyExist;
import ru.nsu.sberlab.blockchain.blockchain_interaction.utils.BlockInfo;
import ru.nsu.sberlab.blockchain.blockchain_interaction.utils.PlayerCoordinates;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapInteractionTest {
    private static final String ownerWifEnvVar = "OWNER_WIF";
    private static final String nodeURLEnvVar = "NODE_URL";
    private static MapInteraction mapInteraction;
    private static String httpURL;
    private static Account account;

    @BeforeAll
    public static void beforeAll() {
        account = Account.fromWIF(System.getenv(ownerWifEnvVar));
        httpURL = System.getenv(nodeURLEnvVar);
    }

    @Order(0)
    @Test
    public void deploymentTest() throws BlockChainException {
        try {
            mapInteraction = new MapInteraction(httpURL, account, "1");
            System.out.println(mapInteraction.getMapContractHash());
            System.out.println(mapInteraction.getStateContractHash());
        } catch (MapAlreadyExist e) {
            System.out.println(e.getMessage());
            mapInteraction = new MapInteraction(httpURL, account, e.getMapContractExistHash(), e.getStateContractExistHash());
        }


    }

    @Order(1)
    @Test
    public void putGetTest() throws Throwable {
        BlockInfo blockInfo = new BlockInfo(1, 2, 3, 4, 5, 6);
        mapInteraction.addChanges(blockInfo);

        ArrayList<BlockInfo> result = mapInteraction.getAllChanges();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(blockInfo);
    }

    @Order(2)
    @Test
    public void clear() throws Throwable {
        mapInteraction.deleteAllChanges();

        ArrayList<BlockInfo> result = mapInteraction.getAllChanges();
        assertThat(result.size()).isEqualTo(0);
    }

    @Order(3)
    @Test
    public void getNChanges() throws Throwable {
        mapInteraction.deleteAllChanges();

        BlockInfo blockInfo = new BlockInfo(1, 2, 3, 4, 5, 6);
        mapInteraction.addChanges(blockInfo);

        BlockInfo blockInfo1 = new BlockInfo(2, 3, 4, 5, 6, 7);
        mapInteraction.addChanges(blockInfo1);

        ArrayList<BlockInfo> result = mapInteraction.getAllChangesWithoutFirstN(1);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(blockInfo1);
    }


    @Order(4)
    @Test
    public void putGetCordsTest() throws Throwable {
        PlayerCoordinates cords = new PlayerCoordinates(1, 223, 444, 123, 4);

        mapInteraction.putPlayerCoordinates(cords);

        assertThat(mapInteraction.getCoordinates()).isEqualTo(cords);

        cords.setZ(158888888);

        mapInteraction.putPlayerCoordinates(cords);

        assertThat(mapInteraction.getCoordinates()).isEqualTo(cords);

    }


}
