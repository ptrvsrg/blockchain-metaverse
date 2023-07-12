package blockchain;


public class Main {


    public static void main(String[] args) throws Throwable {
        BlockInformation blockInformation = new BlockInformation(0,1,2,3,4,5);
        BlockInformation newBlockInformation = new BlockInformation(blockInformation.getBytesPresentation());

        System.out.println(newBlockInformation);

    }
}
