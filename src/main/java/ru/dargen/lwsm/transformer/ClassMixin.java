package ru.dargen.lwsm.transformer;

@FunctionalInterface
public interface ClassMixin {

    byte[] transform(String name, byte[] classBytes);

}
