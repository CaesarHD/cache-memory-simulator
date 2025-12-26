package org.mem.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerBlockPrinter implements BlockPrinter {
    Logger logger = LoggerFactory.getLogger(LoggerBlockPrinter.class);

    @Override
    public void printCache(Cache cache) {
        logger.debug("");
        int index = 0;
        for (MemoryBlock block : cache.getBlocks()) {
            logger.debug("Index {} tag: {}: {}", index, cache.getBlockTag(index), block);
            index++;
        }
        logger.debug("");
    }

    @Override
    public void printMemory(MainMemory mainMemory) {
        int address = 0;
        int numBits = (int) Math.ceil(Math.log(mainMemory.getBlockSize() * mainMemory.getNbOfBlocks()) / Math.log(2));
        for (MemoryBlock block : mainMemory.getBlocks()) {
            String byteAddress = String.format("%" + numBits + "s", Integer.toBinaryString(address)).replace(' ', '0');
            logger.debug("Address {}: {}", byteAddress, block);
            address += mainMemory.getBlockSize();
        }

    }

}
