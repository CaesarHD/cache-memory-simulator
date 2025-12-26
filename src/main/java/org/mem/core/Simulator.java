package org.mem.core;

import org.mem.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static org.mem.Util.ByteUtil.bytesToValue;
import static org.mem.Util.TypeUtil.getVarClass;

public class Simulator {
    private static CacheStatus cacheStatus;
    private static final Logger log = LoggerFactory.getLogger(Simulator.class);

    public static OperationMode OPERATION_MODE = OperationMode.NONE;

    //TODO: consider making these constants with caps as the java standard suggests
    public static final int duration = 30;
    public static final int MAIN_MEMORY_BLOCKS_NB = 64;
    public static final int CACHE_BLOCKS_NB = 4;

    public static final BlockingQueue<Boolean> stepQueue = new LinkedBlockingQueue<>();
    public static final AtomicReference<String> currentInstruction = new AtomicReference<>();
    public static final AtomicReference<Integer> currentAddress = new AtomicReference<>();
    public static final AtomicReference<Boolean> cacheOnFocus = new AtomicReference<>(true);
    public static final AtomicReference<Boolean> mainMemoryOnFocus = new AtomicReference<>(true);

    public static MainMemory mainMemory;
    public static Cache cache;
    public static int numBits;
    private final int blockSize;

    public Simulator(int blockSize) {
        this.blockSize = blockSize;
        mainMemory = new MainMemory(blockSize, MAIN_MEMORY_BLOCKS_NB, new LoggerBlockPrinter());
        numBits = (int) Math.ceil(Math.log(blockSize * MAIN_MEMORY_BLOCKS_NB) / Math.log(2));
        cache = new Cache(CACHE_BLOCKS_NB, blockSize, numBits, new LoggerBlockPrinter());
    }

    public IntPtr newInt(int value) {
        IntPtr intPtr = this.newIntArray(1);
        intPtr.update(value);
        return intPtr;
    }

    public CharPtr newChar(char value) {
        CharPtr charPtr = this.newCharArray(1);
        charPtr.update(value);
        return charPtr;
    }

    public LongPtr newLong(long value) {
        LongPtr longPtr = this.newLongArray(1);
        longPtr.update(value);
        return longPtr;
    }

    public IntPtr newIntArray(int size) {
        int address = mainMemory.allocate(Integer.BYTES, size);
        return IntPtr.fromAddress(address, this);
    }

    public CharPtr newCharArray(int size) {
        int address = mainMemory.allocate(Character.BYTES, size);
        return CharPtr.fromAddress(address, this);
    }

    public LongPtr newLongArray(int size) {
        int address = mainMemory.allocate(Long.BYTES, size);
        return LongPtr.fromAddress(address, this);
    }

    Object load(Ptr<?> var) {
        currentInstruction.set(String.valueOf(InstructionType.LOAD));
        currentAddress.set(var.getAddress());
        cacheAndMemorySync(var.getAddress());
        Class<?> targetClass = getVarClass(var);
        List<Byte> result = cache.load(var.getAddress(), var.getTypeSize());

        return bytesToValue(result, targetClass);
    }

    Object update(Ptr<?> var) {
        currentInstruction.set(String.valueOf(InstructionType.UPDATE));
        currentAddress.set(var.getAddress());
        cacheAndMemorySync(var.getAddress());
        List<Byte> result = cache.update(var.getAddress(), var.getTypeSize(), var.getValue());
        Class<?> targetClass = getVarClass(var);

        return bytesToValue(result, targetClass);
    }

    private static void fetchDataToCache(int address) {
        MemoryBlock block = mainMemory.getAddressBlock(address);
        List<Byte> newCacheLine = new ArrayList<>(block.getContainer());

        cache.insertLine(address, newCacheLine);
    }

    private static void evict(int evictedAddress) {
        MemoryBlock block = cache.getAddressBlock(evictedAddress);
        mainMemory.getAddressBlock(evictedAddress).setContainer(block.getContainer());
    }

    private static void cacheAndMemorySync(int address) {
        mainMemoryOnFocus.set(false);
        cacheOnFocus.set(false);
        simulationMode(Duration.ofMillis(duration));

        if (cache.contains(address)) {
            performHit();
        } else {
            performMiss(address);
        }
    }

    private static void performHit() {
        cacheStatus = CacheStatus.HIT;
        mainMemoryOnFocus.set(false);
        cacheOnFocus.set(true);
        simulationMode(Duration.ofMillis(duration));
    }

    private static void performMiss(int address) {
        cacheStatus = CacheStatus.MISS;
        mainMemoryOnFocus.set(false);
        cacheOnFocus.set(true);

        if (cache.evictionNeeded(address)) {
            performEvict(address);
        }

        performFetch(address);
    }

    private static void performFetch(int address) {
        InstructionType prev = InstructionType.valueOf(currentInstruction.get());
        currentInstruction.set(String.valueOf(prev));
        currentAddress.set(address);
        mainMemoryOnFocus.set(false);
        simulationMode(Duration.ofMillis(duration));
        fetchDataToCache(address);
        simulationMode(Duration.ofMillis(duration));
    }

    private static void performEvict(int address) {
        int evictedAddress = cache.getEvictedAddress(address);

        currentInstruction.set(String.valueOf(InstructionType.EVICT));
        currentAddress.set(evictedAddress);
        simulationMode(Duration.ofMillis(duration));
        mainMemoryOnFocus.set(true);
        simulationMode(Duration.ofMillis(duration));

        evict(evictedAddress);
    }

    private static void simulationMode(Duration delay) {
        switch (OPERATION_MODE) {
            case STEP -> {
                try {
                    stepQueue.take();
                } catch (InterruptedException e) {
                    log.error("Could not read step queue");
                    throw new RuntimeException(e);
                }
            }
            case DELAY -> delay(delay);
            case NONE -> {
            }
            default -> throw new IllegalStateException("Unexpected value: " + OPERATION_MODE);
        }
    }

    void remove(Ptr<?> var) {
        mainMemory.remove(var.getAddress(), var.getTypeSize());
    }

    private static void delay(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static CacheStatus getCacheStatus() {
        return cacheStatus;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Simulator) obj;
        return this.blockSize == that.blockSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockSize);
    }

    @Override
    public String toString() {
        return "Simulator[" +
                "blockSize=" + blockSize + ']';
    }


}
