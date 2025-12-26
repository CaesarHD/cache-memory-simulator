package org.mem.View;

import org.mem.core.Simulator;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

public class SimulationUI extends JFrame {

    public static JLabel crtInstruction;

    public SimulationUI() throws HeadlessException {
        configFrame();
        JPanel panel = new JPanel(null);

        add(panel);

        JScrollPane mainMemory = createMainMemory();
        JScrollPane cacheMemory = createCacheMemory();

        crtInstruction = new JLabel(Simulator.currentInstruction + " " + Simulator.cache.getAddressAsStringOfBits(Simulator.currentAddress.get()));

        crtInstruction.setBounds(20, 20, 500, 30);
        panel.add(crtInstruction);

        panel.add(cacheMemory);
        panel.add(mainMemory);
    }

    public static void updateInstruction() {
        crtInstruction.setText(
                Simulator.currentInstruction + " " + Simulator.cache.getAddressAsStringOfBits(Simulator.currentAddress.get())
        );
    }

    private JScrollPane createCacheMemory() {
        JTable cacheMemory = createCacheMemoryTable();
        JScrollPane scrollPane = new JScrollPane(cacheMemory);

        adjustCacheSize(scrollPane);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustCacheSize(scrollPane);
            }
        });

        return scrollPane;
    }

    private JScrollPane createMainMemory() {
        JTable mainMemoryTable = createMainMemoryTable();
        JScrollPane scrollPane = new JScrollPane(mainMemoryTable);
        adjustMainMemorySize(scrollPane);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustMainMemorySize(scrollPane);
            }
        });

        return scrollPane;
    }

    private JTable createCacheMemoryTable() {
        JTable cache = new JTable(new CacheTableModel());
        cache.setDefaultRenderer(Object.class, new CacheTableRenderer());

        adjustTagIndexOffsetColumnSize(cache);

        return cache;
    }

    private JTable createMainMemoryTable() {
        JTable mainMemory = new JTable(new MemoryTableModel());
        mainMemory.setDefaultRenderer(Object.class, new MemoryTableRenderer());

        adjustAddressColumnSize(mainMemory);

        return mainMemory;
    }

    private static void adjustAddressColumnSize(JTable mainMemory) {
        TableColumn firstColumn = mainMemory.getColumnModel().getColumn(0);
        firstColumn.setPreferredWidth(250);
    }

    private static void adjustTagIndexOffsetColumnSize(JTable cache) {
        for (int i = 0; i < 3; i++) {
            TableColumn column = cache.getColumnModel().getColumn(i);
            column.setPreferredWidth(250);
        }
    }

    private void adjustCacheSize(JScrollPane scrollPane) {
        int heightPadding = getContentPane().getHeight() / 3;
        scrollPane.setBounds(
                (int) (getContentPane().getWidth() - getContentPane().getWidth() / 1.2),
                heightPadding,
                getContentPane().getWidth() / 3,
                getContentPane().getHeight() - 2 * heightPadding
        );
    }

    private void adjustMainMemorySize(JScrollPane scrollPane) {
        int heightPadding = getContentPane().getHeight() / 10;
        scrollPane.setBounds(
                getContentPane().getWidth() - getContentPane().getWidth() / 3,
                heightPadding,
                getContentPane().getWidth() / 4,
                getContentPane().getHeight() - 2 * heightPadding
        );
    }

    private void configFrame() {

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_TYPED) {
                        Simulator.stepQueue.add(true);
                        updateInstruction();
                    }
                    return false;
                });

        setTitle("Cache Memory Simulation");
        setSize(2500, 1200);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
