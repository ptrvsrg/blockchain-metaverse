package blockchain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class BlockInformationTest {


    @Test
    public void ByteIntConversionTest() throws Exception {

        for (int i = 0, someI = 0; i < 1000; i++) {
            someI = (-1) * (someI + 6);
            BlockInformation blockInformation = new BlockInformation(someI, someI + 1, someI + 2, someI + 3, someI + 4, someI + 5);
            BlockInformation newBlockInformation = new BlockInformation(blockInformation.getBytesPresentation());

            assertThat(blockInformation.toString()).isEqualTo(newBlockInformation.toString());
        }
    }
}
