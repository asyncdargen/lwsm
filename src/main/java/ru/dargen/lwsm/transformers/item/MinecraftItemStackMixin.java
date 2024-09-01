package ru.dargen.lwsm.transformers.item;

import lombok.val;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import ru.dargen.lwsm.transformer.AsmClassMixin;
import ru.dargen.lwsm.util.Asm;

import static org.objectweb.asm.Opcodes.*;

public class MinecraftItemStackMixin implements AsmClassMixin {

    @Override
    public ClassNode transform(String name, ClassNode node) {
        Asm.addField(node, ACC_PUBLIC, "bukkitMeta", "Lorg/bukkit/inventory/meta/ItemMeta;");
        Asm.removeField(node, "v", "Lnet/minecraft/nbt/NBTTagCompound;");
        Asm.addField(node, ACC_PUBLIC, "v", "Lnet/minecraft/nbt/NBTTagCompound;");
        Asm.addField(node, ACC_PUBLIC, "lastNBT", "Lnet/minecraft/nbt/NBTTagCompound;");

        Asm.addMethod(node, ACC_PUBLIC, "syncMeta", "(Lorg/bukkit/inventory/meta/ItemMeta;)V", method -> {
            var copyTag = new Label();
            var exit = new Label();

            method.visitVarInsn(ALOAD, 0);
            method.visitVarInsn(ALOAD, 1);
            method.visitFieldInsn(PUTFIELD, "net/minecraft/world/item/ItemStack", "bukkitMeta", "Lorg/bukkit/inventory/meta/ItemMeta;");

            method.visitVarInsn(ALOAD, 0);
            method.visitFieldInsn(GETFIELD, "net/minecraft/world/item/ItemStack", "v", "Lnet/minecraft/nbt/NBTTagCompound;");
            method.visitJumpInsn(IFNONNULL, copyTag);

            method.visitVarInsn(ALOAD, 0);
            method.visitInsn(ACONST_NULL);
            method.visitFieldInsn(PUTFIELD, "net/minecraft/world/item/ItemStack", "lastNBT", "Lnet/minecraft/nbt/NBTTagCompound;");
            method.visitJumpInsn(GOTO, exit);

            Asm.putLabel(method, copyTag, __ -> {
                method.visitVarInsn(ALOAD, 0);
                method.visitVarInsn(ALOAD, 0);
                method.visitFieldInsn(GETFIELD, "net/minecraft/world/item/ItemStack", "v", "Lnet/minecraft/nbt/NBTTagCompound;");
                method.visitFieldInsn(PUTFIELD, "net/minecraft/world/item/ItemStack", "lastNBT", "Lnet/minecraft/nbt/NBTTagCompound;");
            });

            Asm.putLabel(method, exit, __ -> {
                method.visitInsn(RETURN);
            });
        });
        Asm.addMethod(node, ACC_PUBLIC, "checkDirty", "()Z", method -> {
            var dirty = new Label();

            //bukkitMeta is null
            method.visitVarInsn(ALOAD, 0);
            method.visitFieldInsn(GETFIELD, "net/minecraft/world/item/ItemStack", "bukkitMeta", "Lorg/bukkit/inventory/meta/ItemMeta;");
            method.visitJumpInsn(IFNULL, dirty);

            //v (tag) is null
//            method.visitVarInsn(ALOAD, 0);
//            method.visitFieldInsn(GETFIELD, "net/minecraft/world/item/ItemStack", "v", "Lnet/minecraft/nbt/NBTTagCompound;");
//            method.visitJumpInsn(IFNULL, dirty);

            //if not equal
            method.visitVarInsn(ALOAD, 0);
            method.visitFieldInsn(GETFIELD, "net/minecraft/world/item/ItemStack", "v", "Lnet/minecraft/nbt/NBTTagCompound;");
            method.visitVarInsn(ALOAD, 0);
            method.visitFieldInsn(GETFIELD, "net/minecraft/world/item/ItemStack", "lastNBT", "Lnet/minecraft/nbt/NBTTagCompound;");
//            method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
            method.visitJumpInsn(IF_ACMPNE, dirty);

            method.visitInsn(ICONST_0);
            method.visitInsn(IRETURN);

            Asm.putLabel(method, dirty, __ -> {
                method.visitInsn(ICONST_1);
                method.visitInsn(IRETURN);
            });
        });

        return node;
    }

}
