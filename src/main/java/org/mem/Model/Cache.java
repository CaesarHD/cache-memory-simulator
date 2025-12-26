package org.mem.Model;

import org.mem.Util.ByteUtil;
import org.mem.core.Ptr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mem.Util.TypeUtil.getVarClass;

public class Cache extends Memory {
    private final List<Integer> tags;
    private final List<Boolean> validBits;
    private final int offsetSize;
    private final int indexSize;
    private final HashMap<Integer, Integer> mainMemoryAddress;
    private final int mainMemoryAddressBitsSize;

    public Cache(int nbOfBlocks, int blockSize, int mainMemoryAddressBitsSize, BlockPrinter printer) {
        super(nbOfBlocks, blockSize, printer);
        this.offsetSize = (int) (Math.log10(blockSize) / Math.log10(2));
        this.indexSize = (int) (Math.log10(nbOfBlocks) / Math.log10(2));
        this.mainMemoryAddressBitsSize = mainMemoryAddressBitsSize;
        this.mainMemoryAddress = new HashMap<>();
        this.tags = new ArrayList<>(nbOfBlocks);
        this.validBits = new ArrayList<>(nbOfBlocks);

        for (int i = 0; i < nbOfBlocks; i++) {
            tags.add(i, null);
            validBits.add(i, false);
            mainMemoryAddress.put(i, null);
        }
    }

    @Override
    public int getMemoryAddressOfVarAddress(int varAddress) {
        int byteOffset = getAddressOffset(varAddress);
        int blockIndex = getAddressIndex(varAddress);

        return blockIndex * getBlockSize() + byteOffset;
    }

    public boolean contains(int address) {
        int addressIndex = getAddressIndex(address);
        int addressTag = getAddressTag(address);

        return isValidBitTrue(addressIndex) && !tagMiss(addressIndex, addressTag);
    }
    
    private boolean isValidBitTrue(int index) {
        return validBits.get(index);
    }
    
    private boolean tagMiss(int index, int tag) {
        Integer indexTag = tags.get(index);
        return indexTag == null || tag != indexTag;
    }

    public boolean evictionNeeded(int address) {
        int blockIndex = getAddressIndex(address);
        return getBlockTag(blockIndex) != null;
    }

    public int getEvictedAddress(int address) {
        int blockIndex = getAddressIndex(address);
        return getMainMemoryAddressFromIndex(blockIndex);
    }

    private static int extractBitsFromAddress(int address, int offset, int length) {
        int shift = Integer.SIZE - offset - length;
        return (address >>> shift) & ((1 << length) - 1);
    }

    public String getAddressAsStringOfBits(Integer address) {
        String res;
        try {
            res = String.format("%" + mainMemoryAddressBitsSize + "s", Integer.toBinaryString(address)).replace(' ', '0');
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public int getAddressTag(int address) {
        int tagLength = Integer.SIZE - offsetSize - indexSize;
        int tagOffset = 0;

        try {
            return extractBitsFromAddress(address, tagOffset, tagLength);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public int getAddressIndex(int address) {
        int indexOffset = Integer.SIZE - offsetSize - indexSize;

        try {
            return extractBitsFromAddress(address, indexOffset, indexSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getAddressOffset(int address) {
        int offset = Integer.SIZE - this.offsetSize;

        try {
            return extractBitsFromAddress(address, offset, this.offsetSize);
        } catch (Exception e) {
            //TODO: log the try catch blocks
            throw new RuntimeException(e);
        }
    }

    public void insertLine(int address, List<Byte> data) {
        int startAddress = getMemoryAddressOfVarAddress(address);

        getBlockAllocator().writeValue(startAddress, data);
        updateCacheLineFields(address);
    }

    private void updateCacheLineFields(int address) {
        int index = getAddressIndex(address);
        mainMemoryAddress.put(index, address);
        tags.set(index, getAddressTag(address));
        validBits.set(index, true);
    }

    @Override
    public void printBlocks() {
        getPrinter().printCache(this);
    }

    @Override
    public MemoryBlock getAddressBlock(int address) {
        int blockIndex = getAddressIndex(address);
        return getBlocks().get(blockIndex);
    }

    public Object getValue(Ptr<?> var) {
        Class<?> targetClass = getVarClass(var);
        return ByteUtil.bytesToValue(load(var.getAddress(), var.getTypeSize()), targetClass);
    }

    public Integer getBlockTag(int index) {
        return tags.get(index);
    }

    public Integer getMainMemoryAddressFromIndex(int index) {
        return mainMemoryAddress.get(index);
    }

    public Boolean getValidBit(int index) {
        return validBits.get(index);
    }

}
