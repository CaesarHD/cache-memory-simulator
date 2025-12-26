package org.mem.core;

import org.junit.Test;
import org.mem.Model.Cache;
import static org.junit.jupiter.api.Assertions.*;

public class SimulatorTest {
    private static final int MEMORY_BLOCK_BYTES = 8;
    private final Simulator sim = new Simulator(MEMORY_BLOCK_BYTES);
    private final Cache cache = Simulator.cache;

    @Test
    public void loadTest() {
        cache.printBlocks();
        IntPtr a = sim.newInt(25);
        cache.printBlocks();
        LongPtr b = sim.newLong(67443253635L);
        cache.printBlocks();
        IntPtr c = sim.newInt(40);
        cache.printBlocks();
        LongPtr d = sim.newLong(432235324325234L);
        cache.printBlocks();
        LongPtr e = sim.newLong(123234323412L);
        cache.printBlocks();
        LongPtr f = sim.newLong(97934864565L);
        cache.printBlocks();
        LongPtr g = sim.newLong(8176987134545L);

        Integer intInitialValue = (Integer) cache.getValue(a);
        assertEquals(-849383243, intInitialValue);

        cache.printBlocks();
        //MISS
        Integer intValue = a.load();
        cache.printBlocks();

        Integer intValueAfterLoad = (Integer) cache.getValue(a);
        assertEquals(intValue, intValueAfterLoad);

        //HIT
        intValue = c.load();
        assertEquals(c.getValue(), intValue);
        cache.printBlocks();

        Long longInitialValue = (Long) cache.getValue(b);
        assertEquals(g.getValue(), longInitialValue);

        //MISS
        Long longValue = b.load();
        cache.printBlocks();

        Long longValueAfterLoad = (Long) cache.getValue(b);
        assertEquals(longValue, longValueAfterLoad);

        intInitialValue = (Integer) cache.getValue(c);
        assertEquals(c.getValue(), intInitialValue);

        longInitialValue = (Long) cache.getValue(d);
        assertEquals(d.getValue(), longInitialValue);

        //MISS
        longValue = d.load();
        cache.printBlocks();

        longValueAfterLoad = (Long) cache.getValue(d);
        assertEquals(longValue, longValueAfterLoad);

        longInitialValue = (Long) cache.getValue(e);
        assertEquals(e.getValue(), longInitialValue);

        //MISS
        longValue = e.load();
        cache.printBlocks();


        longValueAfterLoad = (Long) cache.getValue(e);
        assertEquals(longValue, longValueAfterLoad);

        //HIT
        intValue = a.load();
        cache.printBlocks();
        assertEquals(a.getValue(), intValue);

        //HIT
        intValue = c.load();
        cache.printBlocks();
        assertEquals(c.getValue(), intValue);

        longInitialValue = (Long) cache.getValue(f);
        assertEquals(171798691865L, longInitialValue);

        //MISS
        longValue = f.load();
        cache.printBlocks();

        longValueAfterLoad = (Long) cache.getValue(f);
        assertEquals(longValue, longValueAfterLoad);

        longInitialValue = (Long) cache.getValue(g);
        assertEquals(b.getValue(), longInitialValue);

        //MISS
        longValue = g.load();
        cache.printBlocks();

        longValueAfterLoad = (Long) cache.getValue(g);
        assertEquals(longValue, longValueAfterLoad);

        longInitialValue = (Long) cache.getValue(f);
        assertEquals(f.getValue(), longInitialValue);

        //HIT
        longValue = f.load();
        cache.printBlocks();

        longValueAfterLoad = (Long) cache.getValue(f);
        assertEquals(longValue, longValueAfterLoad);

        longInitialValue = (Long) cache.getValue(g);
        assertEquals(g.getValue(), longInitialValue);

        //HIT
        longValue = g.load();
        cache.printBlocks();

        longValueAfterLoad = (Long) cache.getValue(g);
        assertEquals(longValue, longValueAfterLoad);
    }



}