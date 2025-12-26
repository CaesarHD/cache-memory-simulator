package org.mem.Util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.engine.config.CachingJupiterConfiguration;

import java.util.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mem.Util.ByteUtil.bytesToValue;

class ByteUtilTest {
    @Test
    void bytesToValueLong() {
        List<Byte> bytes = new ArrayList<>();
        bytes.add((byte) -120);
        bytes.add((byte) 119);
        bytes.add((byte) 102);
        bytes.add((byte) 85);
        bytes.add((byte) 68);
        bytes.add((byte) 51);
        bytes.add((byte) 34);
        bytes.add((byte) 17);
        Long res = bytesToValue(bytes, Long.class);
        assert res.equals(0x1122334455667788L);
    }

    @Test
    void bytesToValueInt() {
        List<Byte> bytes = new ArrayList<>();
        bytes.add((byte) 68);
        bytes.add((byte) 51);
        bytes.add((byte) 34);
        bytes.add((byte) 17);
        bytes.add((byte) 0);
        bytes.add((byte) 0);
        bytes.add((byte) 0);
        bytes.add((byte) 0);
        Integer res = bytesToValue(bytes, Integer.class);
        assert res.equals(0x11223344);
    }

    @Test
    void bytesToValueChar() {
        List<Byte> bytes = new ArrayList<>();
        bytes.add((byte) 65);
        bytes.add((byte) 0);
        bytes.add((byte) 0);
        bytes.add((byte) 0);
        bytes.add((byte) 0);
        bytes.add((byte) 0);
        bytes.add((byte) 0);
        bytes.add((byte) 0);
        Character res = bytesToValue(bytes, Character.class);
        assert res.equals('A');
    }
}