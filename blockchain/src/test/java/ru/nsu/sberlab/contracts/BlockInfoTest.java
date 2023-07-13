package ru.nsu.sberlab.contracts;

import org.junit.jupiter.api.Test;
import ru.nsu.sberlab.contracts.utils.BlockInfo;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.nsu.sberlab.contracts.utils.BlockInfo.BlockInfoByteSize;


public class BlockInfoTest {


    @Test
    public void ByteIntConversionTest() throws Exception {

        for (int i = 0, someI = 0; i < 1000; i++) {
            someI = (-1) * (someI + 6);
            BlockInfo blockInfo = new BlockInfo(someI, someI + 1, someI + 2, someI + 3, someI + 4, someI + 5);
            BlockInfo newBlockInformation = new BlockInfo(blockInfo.getBytesPresentation());

            assertThat(blockInfo.toString()).isEqualTo(newBlockInformation.toString());
        }
    }

    @Test
    public void ArrayConversionTest() throws Exception {
        byte[] array = new byte[3 * BlockInfoByteSize];
        ArrayList<BlockInfo> inputArray = new ArrayList<>(3);

        for (int i = 0, someI = 0; i < 3; i++) {
            someI = (-1) * (someI + 6);
            BlockInfo blockInfo = new BlockInfo(someI, someI + 1, someI + 2, someI + 3, someI + 4, someI + 5);
            inputArray.add(blockInfo);
            System.arraycopy(blockInfo.getBytesPresentation(), 0, array, i* BlockInfoByteSize, BlockInfoByteSize);
        }

        ArrayList<BlockInfo> resArray = BlockInfo.getInfoArrayFromByteRepresentation(array);

        for (int i = 0; i < 3; i++) {
            assertThat(resArray.get(i).toString()).isEqualTo(inputArray.get(i).toString());
        }
    }
}
