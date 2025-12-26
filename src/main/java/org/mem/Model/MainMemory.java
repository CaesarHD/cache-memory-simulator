package org.mem.Model;

public class MainMemory extends Memory {
    public MainMemory(int blockSize, int nbOfBlocks, BlockPrinter printer) {
        super(nbOfBlocks, blockSize, printer);
    }

    @Override
    public int getMemoryAddressOfVarAddress(int varAddress) {
        return varAddress;
    }

    @Override
    public MemoryBlock getAddressBlock(int address) {
        return getBlocks().get(address / getBlockSize());
    }

    @Override
    public void printBlocks() {
        getPrinter().printMemory(this);
    }
}
