package org.mem.core;

public class LongPtr extends Ptr<Long> {
    public LongPtr(Long value, Integer address, Simulator simulator) {
        super(value, address, simulator);
    }

    @Override
    public int getTypeSize() {
        return Long.BYTES;
    }

    public static LongPtr fromAddress(Integer address, Simulator simulator) {
        return new LongPtr(null, address, simulator);
    }

    public static LongPtr fromValue(Long value, Simulator simulator) {
        return new LongPtr(value, null, simulator);
    }


    @Override
    public String toString() {
        return "LongVariable{" +
                "value=" + super.getValue() +
                ", address=" + super.getAddress() +
                '}';
    }
}
