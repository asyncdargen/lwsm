package ru.dargen.lwsm.transformer;

import org.objectweb.asm.tree.ClassNode;
import ru.dargen.lwsm.util.Asm;

@FunctionalInterface
public interface AsmBytesClassMixin extends ClassMixin {

    @Override
    default byte[] transform(String name, byte[] classBytes) {
        var node = Asm.readClassNode(classBytes);
        classBytes = transform(name, node);

        return classBytes;
    }

    byte[] transform(String name, ClassNode node);

}
