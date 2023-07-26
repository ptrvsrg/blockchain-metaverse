package ru.nsu.sberlab.blockchainintegration;

import org.junit.jupiter.api.Test;
import ru.nsu.sberlab.blockchainintegration.utils.BlockCoordinates;
import ru.nsu.sberlab.blockchainintegration.utils.BlockInfo;
import ru.nsu.sberlab.blockchainintegration.utils.PlayerCoordinates;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UtilsTest {

    @Test
    public void BlockCoordinatesTest() throws Exception {
        for (int i = 0; i < 100; i++) {
            BlockCoordinates blockCoordinates = new BlockCoordinates((int) (Math.random() * 1000),
                    (int) (Math.random() * 1000),
                    (int) (Math.random() * 1000), (int) (Math.random() * 1000), (int) (Math.random() * 1000));

            assertThat(new BlockCoordinates(blockCoordinates.serialize())).isEqualTo(blockCoordinates);
        }
    }

    @Test
    public void PlayerCoordinatesTest() throws Exception {
        for (int i = 0; i < 100; i++) {
            PlayerCoordinates playerCoordinates = new PlayerCoordinates((float) (Math.random() * 1000),
                    (float) (Math.random() * 1000),
                    (float) (Math.random() * 1000), (float) (Math.random() * 1000), (float) (Math.random() * 1000));

            assertThat(new PlayerCoordinates(playerCoordinates.serialize())).isEqualTo(playerCoordinates);
        }
    }

    @Test
    public void BlockInfoTest() throws Exception {
        for (int i = 0; i < 100; i++) {
            BlockInfo blockInfo = new BlockInfo((int) (Math.random() * 1000), (int) (Math.random() * 1000),
                    (int) (Math.random() * 1000), (int) (Math.random() * 1000), (int) (Math.random() * 1000),
                    (int) (Math.random() * 1000));

            assertThat(new BlockInfo(blockInfo.serialize())).isEqualTo(blockInfo);
        }
    }
}
