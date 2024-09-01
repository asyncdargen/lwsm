package ru.dargen.lwsm.transformers.plugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import ru.dargen.lwsm.transformer.AsmClassMixin;
import ru.dargen.lwsm.util.Asm;

public class PluginClassLoaderMixin implements AsmClassMixin {

    @Override
    public ClassNode transform(String name, ClassNode node) {
        var init = Asm.findMethod(node, "<init>", null);

        for (AbstractInsnNode instruction : init.instructions) {
            if (instruction instanceof MethodInsnNode insn
                    && insn.getOpcode() == Opcodes.INVOKEVIRTUAL
                    && insn.owner.equals("java/lang/Class")
                    && insn.name.equals("newInstance")
            ) {
                init.instructions.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD,2));
                init.instructions.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/bukkit/plugin/PluginDescriptionFile", "getName", "()Ljava/lang/String;"));
                init.instructions.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/dargen/lwsm/transformers/plugin/KotlinPlugins", "newInstance", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;"));
                init.instructions.remove(instruction);
                break;
            }
        }
        return node;
    }

}
