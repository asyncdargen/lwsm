package ru.dargen.lwsm.transformers.plugin;

import ru.dargen.lwsm.MixinBootstrap;

import java.lang.reflect.Modifier;

public class KotlinPlugins {

    public static Object newInstance(Class<?> pluginClass, String name) throws InstantiationException, IllegalAccessException {
        System.out.println(pluginClass);
        try {
            var field = pluginClass.getDeclaredField("INSTANCE");
            if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isFinal(field.getModifiers())) {
                throw new NoSuchFieldException();
            }
            field.trySetAccessible();
            var plugin = field.get(null);
            MixinBootstrap.LOGGER.warning("Plugin %s using Kotlin object %s".formatted(name, pluginClass.getName()));
            return plugin;
        } catch (Throwable e) {
            return pluginClass.newInstance();
        }
    }

}