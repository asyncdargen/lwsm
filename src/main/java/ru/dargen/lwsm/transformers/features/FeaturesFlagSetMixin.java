package ru.dargen.lwsm.transformers.features;

import org.objectweb.asm.tree.*;
import ru.dargen.lwsm.transformer.AsmClassMixin;
import ru.dargen.lwsm.util.Asm;

import static org.objectweb.asm.Opcodes.*;

public class FeaturesFlagSetMixin implements AsmClassMixin {

    @Override
    public ClassNode transform(String name, ClassNode node) {
        var init = Asm.findMethod(node, "<init>", "(Lnet/minecraft/world/flag/FeatureFlagUniverse;J)V");

        for (AbstractInsnNode instruction : init.instructions) {
            if (instruction instanceof VarInsnNode insn && insn.getOpcode() == LLOAD) {
                init.instructions.insertBefore(instruction, new LdcInsnNode(0b111L));
                init.instructions.remove(instruction);
                break;
            }
        }

        return node;
    }

}
