package ru.dargen.lwsm.transformers.item;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import ru.dargen.lwsm.transformer.AsmClassMixin;
import ru.dargen.lwsm.util.Asm;

import static org.objectweb.asm.Opcodes.*;

public class CraftItemStackMixin implements AsmClassMixin {

    @Override
    public ClassNode transform(String name, ClassNode node) {
        Asm.renameMethod(node, "setItemMeta", "(Lorg/bukkit/inventory/meta/ItemMeta;)Z", "setItemMeta0");
        Asm.addMethod(node, Opcodes.ACC_PUBLIC, "setItemMeta", "(Lorg/bukkit/inventory/meta/ItemMeta;)Z", method -> {
            var process = new Label();
            method.visitVarInsn(ALOAD, 0);
            method.visitFieldInsn(GETFIELD, "org/bukkit/craftbukkit/v1_19_R3/inventory/CraftItemStack", "handle", "Lnet/minecraft/world/item/ItemStack;");
            method.visitJumpInsn(IFNONNULL, process);

            method.visitInsn(ICONST_0);
            method.visitInsn(IRETURN);

            Asm.putLabel(method, process, __ -> {
                method.visitVarInsn(ALOAD, 0);
                method.visitVarInsn(ALOAD, 1);
                method.visitMethodInsn(INVOKEVIRTUAL, "org/bukkit/craftbukkit/v1_19_R3/inventory/CraftItemStack", "setItemMeta0", "(Lorg/bukkit/inventory/meta/ItemMeta;)Z", false);
                method.visitVarInsn(ISTORE, 2);

                method.visitVarInsn(ALOAD, 0);
                method.visitFieldInsn(GETFIELD, "org/bukkit/craftbukkit/v1_19_R3/inventory/CraftItemStack", "handle", "Lnet/minecraft/world/item/ItemStack;");
                method.visitVarInsn(ALOAD, 1);
                method.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/item/ItemStack", "syncMeta", "(Lorg/bukkit/inventory/meta/ItemMeta;)V", false);

                method.visitVarInsn(ILOAD, 2);
                method.visitInsn(IRETURN);
            });
        });

        Asm.removeMethod(node, "getItemMeta", "()Lorg/bukkit/inventory/meta/ItemMeta;");
        Asm.addMethod(node, Opcodes.ACC_PUBLIC, "getItemMeta", "()Lorg/bukkit/inventory/meta/ItemMeta;", method -> {
            var returnStored = new Label();
            var returnNew = new Label();
            var checkDirty = new Label();

            method.visitVarInsn(ALOAD, 0);
            method.visitFieldInsn(GETFIELD, "org/bukkit/craftbukkit/v1_19_R3/inventory/CraftItemStack", "handle", "Lnet/minecraft/world/item/ItemStack;");
            method.visitJumpInsn(IFNONNULL, checkDirty);

            method.visitInsn(ACONST_NULL);
            method.visitInsn(ARETURN);

            Asm.putLabel(method, checkDirty, __ -> {
                method.visitVarInsn(ALOAD, 0);
                method.visitFieldInsn(GETFIELD, "org/bukkit/craftbukkit/v1_19_R3/inventory/CraftItemStack", "handle", "Lnet/minecraft/world/item/ItemStack;");
                method.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/item/ItemStack", "checkDirty", "()Z", false);
                method.visitJumpInsn(IFEQ, returnStored);
            });

            Asm.putLabel(method, returnNew, __ -> {
                method.visitVarInsn(ALOAD, 0);
                method.visitFieldInsn(GETFIELD, "org/bukkit/craftbukkit/v1_19_R3/inventory/CraftItemStack", "handle", "Lnet/minecraft/world/item/ItemStack;");
                method.visitMethodInsn(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/v1_19_R3/inventory/CraftItemStack", "getItemMeta", "(Lnet/minecraft/world/item/ItemStack;)Lorg/bukkit/inventory/meta/ItemMeta;", false);
                method.visitVarInsn(Opcodes.ASTORE, 1);

                method.visitVarInsn(ALOAD, 0);
                method.visitFieldInsn(GETFIELD, "org/bukkit/craftbukkit/v1_19_R3/inventory/CraftItemStack", "handle", "Lnet/minecraft/world/item/ItemStack;");
                method.visitVarInsn(ALOAD, 1);
                method.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/item/ItemStack", "syncMeta", "(Lorg/bukkit/inventory/meta/ItemMeta;)V", false);

                method.visitVarInsn(ALOAD, 1);
                method.visitInsn(Opcodes.ARETURN);
            });

            Asm.putLabel(method, returnStored, __ -> {
                method.visitVarInsn(ALOAD, 0);
                method.visitFieldInsn(GETFIELD, "org/bukkit/craftbukkit/v1_19_R3/inventory/CraftItemStack", "handle", "Lnet/minecraft/world/item/ItemStack;");
                method.visitFieldInsn(GETFIELD, "net/minecraft/world/item/ItemStack", "bukkitMeta", "Lorg/bukkit/inventory/meta/ItemMeta;");
                method.visitInsn(Opcodes.ARETURN);
            });
        });

        return node;
    }

}
