package org.mem.Model;

import java.util.ArrayList;
import java.util.List;

public class MemoryBlock {
    private final List<Byte> container;

    public MemoryBlock(int size) {
        if (size == 0) {
            throw new IllegalArgumentException("aaa");
        }

        container = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            container.add(i, (byte) 0);
        }
    }

    public static int blocksNeeded(int varSizeBits, int blockSize) {
        return (int) Math.ceil((double) varSizeBits / blockSize);
    }

    public synchronized void setContainer(List<Byte> bytes) {
        this.container.clear();
        this.container.addAll(bytes);
    }

    public synchronized List<Byte> getContainer() {
        return this.container;
    }

    public synchronized int getSize() {
        return container.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Byte b : container) {
            sb.append(String.format("%02X ", b & 0xFF));
        }
        return "Data: " + sb.toString().trim();
    }

}
