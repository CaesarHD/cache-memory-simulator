package org.mem.core;

public class IntPtr extends Ptr<Integer> {
    public IntPtr(Integer value, Integer address, Simulator simulator) {
        super(value, address, simulator);
    }

    @Override
    public int getTypeSize() {
        return Integer.BYTES;
    }

    public static IntPtr fromAddress(Integer address, Simulator simulator) {
        return new IntPtr(null, address, simulator);
    }

    public void updateAt(int index, int value) {
        IntPtr tmp = new IntPtr(value, getAddress() + (index * getTypeSize()), getSimulator());
        tmp.update(value);
    }

    public int loadFrom(int index) {
        IntPtr tmp = IntPtr.fromAddress(getAddress() + (index * getTypeSize()), getSimulator());
        return tmp.load();
    }

    @Override
    public String toString() {
        return "IntVariable{" +
                "value=" + super.getValue() +
                ", address=" + super.getAddress() +
                '}';
    }
}
