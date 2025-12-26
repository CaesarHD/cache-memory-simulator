package org.mem.core;

import java.util.List;

public interface BlockAllocator {
    int allocate(int typeSize, int nbOfElements);
    void deallocate(long address, int size);
    void writeValue(int startBlockAddress, List<Byte> valueInBytes);
    List<Byte> readValue(int startAddress, int size);
}
