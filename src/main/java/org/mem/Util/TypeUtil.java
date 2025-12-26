package org.mem.Util;

import org.mem.core.Ptr;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeUtil {

    public static Class<?> getVarClass(Ptr<?> var) {
        Type superclass = var.getClass().getGenericSuperclass();
        Class<?> targetClass = null;

        if (superclass instanceof ParameterizedType parameterizedType) {
            Type t = parameterizedType.getActualTypeArguments()[0];
            if (t instanceof Class<?> c) {
                targetClass = c;
            }
        }
        return targetClass;
    }
}
