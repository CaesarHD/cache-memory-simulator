package org.mem.core;

public abstract class Ptr<T> {
    private T value;
    private int address;
    private final Simulator simulator;

    public Ptr(T value, Integer address, Simulator simulator) {
        this.simulator = simulator;
        this.value = value;
        this.address = address;
        if(value != null) {
            simulator.update(this);
        }
    }

    void setValue() {
        simulator.update(this);
    }

    void setAddress(int address) {
        this.address = address;
    }


    public T load() {
        this.value = (T) simulator.load(this);
        return value;
    }
    
    public void update(T value) {
        this.value = value;
        this.value = (T) simulator.update(this);
    }

    public Integer getAddress() {
        return address;
    }

    public void free() {
        simulator.remove(this);
    }

    public abstract int getTypeSize();

     public T getValue() {
        return this.value;
    }

    private void setValue(T value) {
        this.value = value;
    }

    public Simulator getSimulator() {
        return simulator;
    }


    @Override
    public String toString() {
        return "Variable{" +
                "value=" + value +
                ", address=" + address +
                '}';
    }
}
