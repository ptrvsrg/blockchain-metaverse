package blockchain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static blockchain.BlockInformation.BlockInformationByteSize;
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

    @Test
    public void ArrayConversionTest() throws Exception {
        byte[] array = new byte[3 * BlockInformationByteSize];
        ArrayList<BlockInformation> inputArray = new ArrayList<>(3);

        for (int i = 0, someI = 0; i < 3; i++) {
            someI = (-1) * (someI + 6);
            BlockInformation blockInformation = new BlockInformation(someI, someI + 1, someI + 2, someI + 3, someI + 4, someI + 5);
            inputArray.add(blockInformation);
            System.arraycopy(blockInformation.getBytesPresentation(), 0, array, i* BlockInformationByteSize, BlockInformationByteSize);
        }

        ArrayList<BlockInformation> resArray = BlockInformation.getInfArrayFromByteRepresentation(array);

        for (int i = 0; i < 3; i++) {
            assertThat(resArray.get(i).toString()).isEqualTo(inputArray.get(i).toString());
        }
    }
}
