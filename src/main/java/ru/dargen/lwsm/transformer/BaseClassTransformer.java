package ru.dargen.lwsm.transformer;

import lombok.Getter;
import lombok.SneakyThrows;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import ru.dargen.lwsm.MixinBootstrap;
import ru.dargen.lwsm.util.Asm;

import java.awt.*;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class BaseClassTransformer implements ClassFileTransformer {

    @Getter
    private final Map<String, ClassMixin> transformerMap = new HashMap<>();
    private final Path outPath = Path.of(".lwsm-out");

    {
        try {
            Files.createDirectories(outPath);
        } catch (IOException e) {
        }
    }

    @SneakyThrows
    @Override
    public byte[] transform(
            ClassLoader loader, String classFileName,
            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {
        var className = classFileName.replace('/', '.');

        var transformer = transformerMap.get(className);

        if (transformer == null) {
            return classfileBuffer;
        }

        var transformedBytes = classfileBuffer;
        try {
            long start = System.currentTimeMillis();
            transformedBytes = transformer.transform(classFileName, transformedBytes);
            System.out.printf("[LWSM] Class %s transformed in %s ms%n", className, System.currentTimeMillis() - start);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if (transformedBytes != null) {
            var path = outPath.resolve(className + ".class");
            Files.write(path, transformedBytes);
        }

        return transformedBytes == null ? classfileBuffer : transformedBytes;
    }

    public void addMixin(String className, ClassMixin transformer) {
        transformerMap.put(className, transformer);
    }

}
