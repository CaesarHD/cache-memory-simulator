package org.mem.View;

import org.mem.core.Simulator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static org.mem.Model.CacheStatus.HIT;

public class CacheTableRenderer extends DefaultTableCellRenderer {
    private final Font font = new Font("SansSerif", Font.PLAIN, 24);
    private final int cacheLineSize = Simulator.cache.getBlockSize();

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        c.setFont(font);

        table.setRowHeight(60);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setFont(font);

        setHorizontalAlignment(SwingConstants.CENTER);

        int currentAddressIndex = Simulator.cache.getAddressIndex(Simulator.currentAddress.get());
        int currentTableIndex = Simulator.cache.getAddressIndex(row * cacheLineSize);

        if (currentTableIndex == currentAddressIndex && Simulator.cacheOnFocus.get()) {
            if (Simulator.getCacheStatus() == HIT) {
                c.setBackground(Color.GREEN);
            } else {
                c.setBackground(Color.RED);
            }

        } else {
            c.setBackground(Color.DARK_GRAY);
        }

        return c;
    }

}
