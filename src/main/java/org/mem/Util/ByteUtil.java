package org.mem.Util;

import java.util.ArrayList;
import java.util.List;

public class ByteUtil {

    public static List<Byte> valueToBytes(Object value, int numBytes) {
        long longValue;

        if (value instanceof Number n) {
            longValue = n.longValue();
        } else if (value instanceof Character c) {
            longValue = c;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }

        List<Byte> bytes = new ArrayList<>();
        for (int i = 0; i < numBytes; i++) {
            bytes.add((byte) ((longValue >> (8 * i)) & 0xFF));
        }

        return bytes;
    }

    @SuppressWarnings("unchecked")
    public static <T> T bytesToValue(List<Byte> bytes, Class<T> type) {
        long value = 0L;

        for (int i = 0; i < bytes.size(); i++) {
            value |= ((long) bytes.get(i) & 0xFF) << (8 * i);
        }

        if(type.equals(Integer.class)) {
            return (T) Integer.valueOf((int) value);
        } else if (type.equals(Long.class)) {
            return (T) Long.valueOf(value);
        } else if(type.equals(Character.class)) {
            return (T) Character.valueOf((char) value);
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

}
