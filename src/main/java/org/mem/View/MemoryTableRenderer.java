package org.mem.View;

import org.mem.core.Simulator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MemoryTableRenderer extends DefaultTableCellRenderer {

    private final Font font = new Font("SansSerif", Font.PLAIN, 24);
    private final int memoryLineSize = Simulator.mainMemory.getBlockSize();

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        //TODO: Refactor this duplicated code block
        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        c.setFont(font);

        table.setRowHeight(60);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setFont(font);

        setHorizontalAlignment(SwingConstants.CENTER);

        if ((row * memoryLineSize) == Simulator.currentAddress.get() && Simulator.mainMemoryOnFocus.get()) {
            c.setBackground(Color.GREEN);
        } else {
            c.setBackground(Color.DARK_GRAY);
        }

        return c;
    }
}
