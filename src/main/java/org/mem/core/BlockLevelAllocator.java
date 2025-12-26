package org.mem.core;

import org.mem.Model.AddressState;
import org.mem.Model.MemoryBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.mem.Model.AddressState.FREE;
import static org.mem.Model.AddressState.USED;
import static org.mem.Model.MemoryBlock.blocksNeeded;

public final class BlockLevelAllocator implements BlockAllocator {
    private final List<MemoryBlock> blocks;
    private final int memoryBlockSize;
    private final HashMap<MemoryBlock, AddressState> blockState;
    private final HashMap<MemoryBlock, Integer> gap;

    public BlockLevelAllocator(List<MemoryBlock> blocks) {
        this.blocks = blocks;
        this.memoryBlockSize = blocks.getFirst().getSize();
        gap = new HashMap<>();
        blockState = new HashMap<>();
        int blocksSize = blocks.size();

        for (MemoryBlock block : blocks) {
            gap.put(block, blocksSize);
            blocksSize--;
            blockState.put(block, FREE);
        }
    }

    @Override
    public int allocate(int typeSize, int nbOfElements) {
        int varSizeInBlocks = blocksNeeded(typeSize * nbOfElements, memoryBlockSize);
        int address = findFreeBlockFor(varSizeInBlocks);
        reserveBlocks(address, varSizeInBlocks);
        return address;
    }

    @Override
    public void deallocate(long address, int size) {
        int allocatedBlocks = blocksNeeded(size, memoryBlockSize);
        int nextAddress = (int) (address + allocatedBlocks);

        IntStream.range((int) address, nextAddress)
                .forEach(i -> blockState.put(this.blocks.get(i), FREE));

        mergeFreeBlocks(nextAddress);
    }

    private void mergeFreeBlocks(int nextAddress) {
        if (nextAddress >= blocks.size()) {
            return;
        }

        MemoryBlock block = this.blocks.get(nextAddress);
        int freeBlocksToMerge = gap.get(block);

        for (int i = nextAddress - 1; i >= 0; i--) {
            block = blocks.get(i);
            if (blockState.get(block) != FREE) return;
            freeBlocksToMerge++;
            gap.put(block, freeBlocksToMerge);
        }
    }

    private int findFreeBlockFor(int size) {
        int address = 0;
        for (MemoryBlock block : this.blocks) {
            if (gap.get(block) >= size) {
                return address;
            }
            address++;
        }
        throw new RuntimeException("Not enough space in main memory");
    }

    private void reserveBlocks(int startAddress, int varSizeInBlocks) {
        for (int i = startAddress; i < startAddress + varSizeInBlocks; i++) {
            gap.put(blocks.get(i), 0);
            blockState.put(blocks.get(i), USED);
        }
    }

    public void writeValue(int startBlockAddress, List<Byte> valueInBytes) {
        int blockIndex = startBlockAddress;
        int byteOffset = 0;

        for (Byte value : valueInBytes) {
            MemoryBlock currentBlock = blocks.get(blockIndex);
            currentBlock.getContainer().set(byteOffset, value);

            byteOffset++;
            if (byteOffset >= memoryBlockSize) {
                blockIndex++;
                byteOffset = 0;
            }
        }
    }

    @Override
    public List<Byte> readValue(int startAddress, int size) {
        int blocksToRead = blocksNeeded(size, memoryBlockSize);

        List<Byte> result = new ArrayList<>(size);

        for (int addr = startAddress; addr < startAddress + blocksToRead; addr++) {
            MemoryBlock block = blocks.get(addr);
            result.addAll(block.getContainer());
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BlockLevelAllocator) obj;
        return Objects.equals(this.blocks, that.blocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blocks);
    }

    @Override
    public String toString() {
        return "BlockLevelAllocator[" +
                "blocks=" + blocks + ']';
    }

}
