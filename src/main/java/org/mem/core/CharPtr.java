package org.mem.core;

public class CharPtr extends Ptr<Character> {
    public CharPtr(Character value, Integer address, Simulator simulator) {
        super(value, address, simulator);
    }

    @Override
    public int getTypeSize() {
        return Character.BYTES;
    }

    public static CharPtr fromValue(Character value, Simulator simulator) {
        return new CharPtr(value, null, simulator);
    }

    public static CharPtr fromAddress(Integer address, Simulator simulator) {
        return new CharPtr(null, address, simulator);
    }


    @Override
    public String toString() {
        return "CharVariable{" +
                "value='" + super.getValue() + "', " +
                "address=" + super.getAddress() +
                '}';
    }
}
