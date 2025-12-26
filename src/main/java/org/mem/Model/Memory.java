package org.mem.Model;

import org.mem.Util.ByteUtil;
import org.mem.core.BlockAllocator;
import org.mem.core.ByteLevelAllocator;

import java.util.ArrayList;
import java.util.List;

public abstract class Memory {
    private final int blockSize;
    private final List<MemoryBlock> blocks;
    private final BlockPrinter printer;
    private final BlockAllocator blockAllocator;

    public Memory(int nbOfBlocks, int blockSize, BlockPrinter printer) {
        blocks = new ArrayList<>(nbOfBlocks);
        this.blockSize = blockSize;
        this.printer = printer;
        for (int i = 0; i < nbOfBlocks; i++) {
            MemoryBlock memoryBlock = new MemoryBlock(blockSize);
            blocks.add(i, memoryBlock);
        }
        this.blockAllocator = new ByteLevelAllocator(blocks);
    }

    public int allocate(int typeSize, int nbOfElements) {
        return getBlockAllocator().allocate(typeSize, nbOfElements);
    }

    public List<Byte> load(int address, int typeSize) {
        int cacheAddress = getMemoryAddressOfVarAddress(address);
        return getBlockAllocator().readValue(cacheAddress, typeSize);
    }

    public List<Byte> update(int address, int typeSize, Object newValue) {
        List<Byte> valueInBytes = ByteUtil.valueToBytes(newValue, typeSize);
        if (valueInBytes.size() > typeSize) {
            throw new IllegalArgumentException("Value is too large");
        }
        int startAddress = getMemoryAddressOfVarAddress(address);
        getBlockAllocator().writeValue(startAddress, valueInBytes);
        return valueInBytes;
    }

    public void remove(int address, int size) {
        int startAddress = getMemoryAddressOfVarAddress(address);
        getBlockAllocator().deallocate(startAddress, size);
    }

    public abstract MemoryBlock getAddressBlock(int address);

    public abstract int getMemoryAddressOfVarAddress(int varAddress);

    public abstract void printBlocks();

    public BlockPrinter getPrinter() {
        return printer;
    }

    public int getNbOfBlocks() {
        return blocks.size();
    }

    public int getBlockSize() {
        return blockSize;
    }

    public List<MemoryBlock> getBlocks() {
        return blocks;
    }

    public BlockAllocator getBlockAllocator() {
        return blockAllocator;
    }
}
