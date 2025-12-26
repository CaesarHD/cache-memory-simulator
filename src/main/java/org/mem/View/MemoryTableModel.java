package org.mem.View;

import org.mem.core.Simulator;

import javax.swing.table.AbstractTableModel;

public class MemoryTableModel extends AbstractTableModel {

    private final int memoryLineSize = Simulator.mainMemory.getBlockSize();
    private final int nbOfBlocks = Simulator.MAIN_MEMORY_BLOCKS_NB;
    private final int numBits = (int) Math.ceil(Math.log(memoryLineSize * nbOfBlocks) / Math.log(2));

    @Override
    public int getRowCount() {
        return nbOfBlocks;
    }

    @Override
    public int getColumnCount() {
        return memoryLineSize + 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int address = rowIndex * memoryLineSize;

        if (columnIndex == 0) {
            return String.format("%" + numBits + "s", Integer.toBinaryString(address)).replace(' ', '0');
        }

        int byteOffset = columnIndex - 1;
        int byteAddress = address + byteOffset;
        Byte value = Simulator.mainMemory.load(byteAddress, Byte.BYTES).getFirst();
        return String.format("%02X", value);

    }

    @Override
    public String getColumnName(int column) {

        if (column == 0) {
            return "Address";
        }

        return "B" + (column - 1);
    }
}
