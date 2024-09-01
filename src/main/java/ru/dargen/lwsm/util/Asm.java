package ru.dargen.lwsm.util;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

import java.util.function.Consumer;
import java.util.function.Predicate;

@UtilityClass
public class Asm {

    public InsnList buildInstructions(Consumer<InsnList> builder) {
        var list = new InsnList();
        builder.accept(list);
        return list;
    }

    public AbstractInsnNode findNode(InsnList list, Predicate<AbstractInsnNode> predicate) {
        for (AbstractInsnNode node : list) {
            if (predicate.test(node)) {
                return node;
            }
        }

        return null;
    }

    public ClassNode readClassNode(byte[] classBytes) {
        var classNode = new ClassNode();
        var classReader = new ClassReader(classBytes);

        classReader.accept(classNode, 0);

        return classNode;
    }

    public byte[] getClassNodeBytes(ClassNode node) {
        var writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        return writer.toByteArray();
    }

    public void addField(ClassNode node, int modifiers, String fieldName, String fieldDescriptor) {
        addField(node, modifiers, fieldName, fieldDescriptor, null);
    }

    public void addField(ClassNode node, int modifiers, String fieldName, String fieldDescriptor, Object value) {
        var field = new FieldNode(modifiers, fieldName, fieldDescriptor, null, value);
        node.fields.add(field);
    }

    public void removeField(ClassNode node, String fieldName, String descriptor) {
        node.fields.removeIf(method -> method.name.equals(fieldName) && (descriptor == null || method.desc.equals(descriptor)));
    }

    public FieldNode findField(ClassNode node, String fieldName, String descriptor) {
        for (FieldNode field : node.fields) {
            if (field.name.equals(fieldName) && (descriptor == null || field.desc.equals(descriptor))) {
                return field;
            }
        }

        return null;
    }

    public void resetFieldModifiers(ClassNode node, String fieldName, String descriptor, int modifiers) {
        findField(node, fieldName, descriptor).access = modifiers;
    }

    public void addMethod(ClassNode node, int modifiers, String methodName, String methodDescriptor, Consumer<MethodNode> code) {
        var method = new MethodNode(modifiers, methodName, methodDescriptor, null, null);

        method.visitCode();
        code.accept(method);
        method.visitEnd();

        node.methods.add(method);
    }

    public void removeMethod(ClassNode node, String methodName, String descriptor) {
        node.methods.removeIf(method -> method.name.equals(methodName) && (descriptor == null || method.desc.equals(descriptor)));
    }

    public MethodNode findMethod(ClassNode node, String methodName, String descriptor) {
        for (MethodNode method : node.methods) {
            if (method.name.equals(methodName) && (descriptor == null || method.desc.equals(descriptor))) {
                return method;
            }
        }

        return null;
    }

    public void renameMethod(ClassNode node, String methodName, String descriptor, String newMethodName) {
        var method = findMethod(node, methodName, descriptor);
        method.name = newMethodName;
    }

    public Label putLabel(MethodNode node, Label label, Consumer<MethodNode> code) {
        node.visitLabel(label);
        code.accept(node);

        return label;
    }

    public Label putLabel(MethodNode node, Consumer<MethodNode> code) {
        return putLabel(node, new Label(), code);
    }

}
