package org.mem.core;

import org.mem.Model.AddressState;
import org.mem.Model.MemoryBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import static org.mem.Model.AddressState.FREE;
import static org.mem.Model.AddressState.USED;

public class ByteLevelAllocator implements BlockAllocator {

    private final List<MemoryBlock> blocks;
    private final int memoryBlockSize;
    private final HashMap<Integer, Integer> gap;
    private final HashMap<Integer, AddressState> addressStates;


    public ByteLevelAllocator(List<MemoryBlock> blocks) {
        this.blocks = blocks;
        this.memoryBlockSize = blocks.getFirst().getSize();
        this.addressStates = new HashMap<>();
        this.gap = new HashMap<>();
        int address = 0;
        int nbOfBytes = blocks.size() * memoryBlockSize;
        for (MemoryBlock block : blocks) {
            for (Byte _ : block.getContainer()) {
                gap.put(address++, nbOfBytes--);
            }
        }
    }

    @Override
    public int allocate(int typeSize, int nbOfElements) {
        int address = findFreeBlockFor(typeSize, nbOfElements);
        reserveSpace(address, typeSize * nbOfElements);
        return address;
    }

    private int findFreeBlockFor(int typeSize, int nbOfElements) {
        int address = 0;

        for (MemoryBlock block : blocks) {
            for (Byte _ : block.getContainer()) {

                if (isFreeSpace(typeSize * nbOfElements, address) && fitInOneBlock(address, typeSize)) {
                    return address;
                }
                address++;
            }
        }
        throw new RuntimeException("Not enough space in main memory");
    }

    private boolean isFreeSpace(int size, int address) {
        return gap.get(address) >= size;
    }

    private boolean fitInOneBlock(int address, int size) {
        int blockOffset = address % memoryBlockSize;
        return (memoryBlockSize - blockOffset) >= size;
    }

    private void reserveSpace(int startAddress, int size) {
        for (int address = startAddress; address < startAddress + size; address++) {
            gap.put(address, 0);
            addressStates.put(address, USED);
        }
    }

    public void writeValue(int startAddress, List<Byte> valueInBytes) {
        int address = startAddress;

        for (Byte value : valueInBytes) {
            int blockIndex = address / memoryBlockSize;
            int byteOffset = address % memoryBlockSize;

            MemoryBlock block = blocks.get(blockIndex);
            block.getContainer().set(byteOffset, value);

            address++;
        }
    }

    public List<Byte> readValue(int startAddress, int size) {
        List<Byte> result = new ArrayList<>(size);

        int address = startAddress;

        for (int i = 0; i < size; i++, address++) {
            int blockIndex = address / memoryBlockSize;
            int byteOffset = address % memoryBlockSize;

            MemoryBlock block = blocks.get(blockIndex);
            result.add(block.getContainer().get(byteOffset));
        }

        return result;
    }

    @Override
    public void deallocate(long address, int size) {
        int nextAddress = (int) (address + size);

        IntStream.range((int) address, nextAddress)
                .forEach(i -> addressStates.put(i, FREE));

        mergeFreeBlocks(nextAddress);
    }

    private void mergeFreeBlocks(int nextAddress) {
        if (nextAddress >= blocks.size()) {
            return;
        }

        int freeBlocksToMerge = gap.get(nextAddress);

        for (int address = nextAddress - 1; address >= 0; address--) {
            if (addressStates.get(address) != FREE) return;
            freeBlocksToMerge++;
            gap.put(address, freeBlocksToMerge);
        }
    }

}
