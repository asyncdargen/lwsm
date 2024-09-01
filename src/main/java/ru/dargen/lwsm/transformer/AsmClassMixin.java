package ru.dargen.lwsm.transformer;

import org.objectweb.asm.tree.ClassNode;
import ru.dargen.lwsm.util.Asm;

@FunctionalInterface
public interface AsmClassMixin extends ClassMixin {

    @Override
    default byte[] transform(String name, byte[] classBytes) {
        var node = Asm.readClassNode(classBytes);
        node = transform(name, node);

        return node == null ? null : Asm.getClassNodeBytes(node);
    }

    ClassNode transform(String name, ClassNode node);

}
