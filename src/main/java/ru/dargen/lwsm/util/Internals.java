package ru.dargen.lwsm.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;

@UtilityClass
public class Internals {

    public final Unsafe UNSAFE = findUnsafe();

    @SneakyThrows
    public Unsafe findUnsafe() {
        var field = Unsafe.class.getDeclaredField("theUnsafe");
        field.trySetAccessible();
        return (Unsafe) field.get(null);
    }

}
