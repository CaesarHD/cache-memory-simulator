package org.mem.View;

import org.mem.Model.Cache;
import org.mem.core.Simulator;

import javax.swing.table.AbstractTableModel;

public class CacheTableModel extends AbstractTableModel {
    private final Cache cache = Simulator.cache;
    private final int cacheLineSize = cache.getBlockSize();

    @Override
    public int getRowCount() {
        return Simulator.CACHE_BLOCKS_NB;
    }

    @Override
    public int getColumnCount() {
        return cacheLineSize + 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int address = rowIndex * cacheLineSize;
        int cacheIndex = cache.getAddressIndex(address);

        if (columnIndex == 0) {
            return cache.getValidBit(cacheIndex) ? "1" : "0";
        }

        if (columnIndex == 1) {

            String addressTag;
            Integer mainMemoryAddressFromIndex = cache.getMainMemoryAddressFromIndex(cacheIndex);
            if (mainMemoryAddressFromIndex != null) {
                int tag = cache.getAddressTag(mainMemoryAddressFromIndex);
                addressTag = String.format("%3s", Integer.toBinaryString(tag))
                        .replace(' ', '0');
            } else {
                addressTag = String.valueOf(cache.getBlockTag(cacheIndex));
            }
            return addressTag;
        }

        if (columnIndex == 2) {
            int offset = cache.getAddressOffset(address);
            return String.format("%3s", Integer.toBinaryString(offset))
                    .replace(' ', '0');
        }

        int byteOffset = columnIndex - 3;
        int byteAddress = address + byteOffset;
        Byte value = cache.load(byteAddress, Byte.BYTES).getFirst();
        return String.format("%02X", value);
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "Valid bit";
            case 1 -> "Tag";
            case 2 -> "Offset";
            default -> "B" + (column - 1);
        };

    }
}
