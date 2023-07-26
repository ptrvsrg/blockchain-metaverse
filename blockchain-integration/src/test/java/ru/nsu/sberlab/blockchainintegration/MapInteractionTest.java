package ru.nsu.sberlab.blockchainintegration;

import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import org.junit.jupiter.api.*;
import ru.nsu.sberlab.blockchainintegration.utils.BlockInfo;
import ru.nsu.sberlab.blockchainintegration.utils.PlayerCoordinates;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapInteractionTest {
    private static final String ownerWifEnvVar = "TEST_OWNER_WIF";
    private static final String nodeURLEnvVar = "TEST_NODE_URL";
    private static final String mapContractHashEnvVar = "TEST_MAP_HASH";
    private static final String stateContractHashEnvVar = "TEST_STATE_HASH";

    private static MapInteraction mapInteraction;

    @BeforeAll
    public static void beforeAll() {
        Account account = Account.fromWIF(System.getenv(ownerWifEnvVar));
        String httpURL = System.getenv(nodeURLEnvVar);
        Hash160 mapContractHash = new Hash160(System.getenv(mapContractHashEnvVar));
        Hash160 stateContractHash = new Hash160(System.getenv(stateContractHashEnvVar));

        mapInteraction = new MapInteraction(httpURL, account, mapContractHash, stateContractHash);
    }

    @Order(2)
    @Test
    public void putGetTest() throws Throwable {
        BlockInfo blockInfo = new BlockInfo(1, 2, 3, 4, 5, 6);
        mapInteraction.addChanges(blockInfo);

        ArrayList<BlockInfo> result = mapInteraction.getAllChanges();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(blockInfo);
    }

    @Order(1)
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
