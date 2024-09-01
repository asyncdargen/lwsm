package ru.dargen.lwsm;

import ru.dargen.lwsm.transformer.BaseClassTransformer;
import ru.dargen.lwsm.transformers.features.FeaturesFlagSetMixin;
import ru.dargen.lwsm.transformers.item.CraftItemStackMixin;
import ru.dargen.lwsm.transformers.item.MinecraftItemStackMixin;
import ru.dargen.lwsm.transformers.plugin.PluginClassLoaderMixin;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class MixinBootstrap {

    public static Logger LOGGER = Logger.getLogger("LWSM");
    private static final BaseClassTransformer TRANSFORMER = new BaseClassTransformer();
    
    public static void main(String[] args) {

    }

    public static void premain(String args, Instrumentation inst) {
        inst.appendToSystemClassLoaderSearch(mixinJar());
        inst.appendToBootstrapClassLoaderSearch(mixinJar());
        initMixins();

        System.out.println("[LWSM] Transformers:");
        TRANSFORMER.getTransformerMap().keySet().forEach(clazz -> System.out.printf("[LWSM] - %s%n", clazz));
        inst.addTransformer(TRANSFORMER, true);
    }

    private static void printMixins() {

    }

    private static void initMixins() {
        TRANSFORMER.addMixin("net.minecraft.world.item.ItemStack", new MinecraftItemStackMixin());
        TRANSFORMER.addMixin("org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack", new CraftItemStackMixin());
        TRANSFORMER.addMixin("net.minecraft.world.flag.FeatureFlagSet", new FeaturesFlagSetMixin());
        TRANSFORMER.addMixin("org.bukkit.plugin.java.PluginClassLoader", new PluginClassLoaderMixin());
    }

    private static JarFile mixinJar() {
        try {
            var file = MixinBootstrap.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            return new JarFile(file);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

}
