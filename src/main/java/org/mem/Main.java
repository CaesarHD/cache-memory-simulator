package org.mem;

import org.mem.View.SimulationUI;
import org.mem.core.IntPtr;
import org.mem.core.LongPtr;
import org.mem.core.OperationMode;
import org.mem.core.Simulator;

import javax.swing.*;
import java.util.Random;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static Simulator sim = new Simulator(8);

    public static void printIntArray(IntPtr array, IntPtr size) {
        IntPtr index = sim.newInt(0);

        while (index.load() < size.load()) {
            System.out.print(array.loadFrom(index.load()) + " ");
            index.update(index.load() + 1);
        }
        System.out.println();
    }

    private static void simpleSumExample() {
        Simulator.OPERATION_MODE = OperationMode.NONE;

//        fill cache
        for (int i = 0; i < 4; i++) {
            sim.newLong(i+1);
        }

        Simulator.OPERATION_MODE = OperationMode.STEP;

        LongPtr a = sim.newLong(7);
        LongPtr b = sim.newLong(5);
        LongPtr s = sim.newLong(0);

        s.update(a.load() + b.load());
    }

    public static void simpleSumExample2() {
        Simulator.OPERATION_MODE = OperationMode.NONE;

        IntPtr a = sim.newIntArray(5);
        IntPtr sum = sim.newInt(0);

        for (int i = 0; i < 10; i++) {
            a.updateAt(i, i + 1);
        }

        for (int i = 0; i < 10; i++) {
            sum.update(sum.load() + a.loadFrom(i));
        }

        System.out.println(sum.load());
    }

    public static void sortExample() {
        IntPtr size = sim.newInt(5);
        IntPtr index = sim.newInt(0);
        IntPtr array = sim.newIntArray(size.load());

        Random rand = new Random();
        while (index.load() < size.load()) {
            array.updateAt(index.load(), rand.nextInt(100));
            index.update(index.load() + 1);
        }

        printIntArray(array, size);

        bubbleSort(size, array);

        printIntArray(array, size);
    }

    public static void bubbleSort(IntPtr size, IntPtr array) {
        IntPtr aux = sim.newInt(0);
        IntPtr i = sim.newInt(0);
        IntPtr j = sim.newInt(0);

        for (; i.load() < size.load() - 1; i.update(i.load() + 1)) {

            j.update(0);
            for (; j.load() < size.load() - 1 - i.load(); j.update(j.load() + 1)) {

                if (array.loadFrom(j.load()) > array.loadFrom(j.load() + 1)) {

                    aux.update(array.loadFrom(j.load()));
                    array.updateAt(j.load(), array.loadFrom(j.load() + 1));
                    array.updateAt(j.load() + 1, aux.load());
                }
            }
        }
    }

    static void main() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        SwingUtilities.invokeLater(SimulationUI::new);
//        simpleSumExample();
        simpleSumExample2();
    }

}