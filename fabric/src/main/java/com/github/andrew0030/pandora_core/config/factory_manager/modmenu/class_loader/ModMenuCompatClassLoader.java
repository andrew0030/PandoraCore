package com.github.andrew0030.pandora_core.config.factory_manager.modmenu.class_loader;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public class ModMenuCompatClassLoader extends ClassLoader {
    private final Map<String, byte[]> stubs = new HashMap<>();
    private final Set<String> modmenuFactoryPaths = new HashSet<>();

    private ModMenuCompatClassLoader(ClassLoader parent) {
        super(parent);
    }

    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static ModMenuCompatClassLoader create(Set<String> modmenuFactoryPaths) {
        // Creates a new class loader instance, using the games class loader as the parent
        ModMenuCompatClassLoader loader = new ModMenuCompatClassLoader(Minecraft.class.getClassLoader());
        // Registers the custom modmenu stubs (minimal in-memory bytecode definitions), to the class loader
        loader.stubs.put("com.terraformersmc.modmenu.api.ModMenuApi", ModMenuStubGenerator.generateModMenuApiStub());
        loader.stubs.put("com.terraformersmc.modmenu.api.ConfigScreenFactory", ModMenuStubGenerator.generateConfigScreenFactoryStub());
        // Registers the classes this loader should load, rather than delegating to the parent loader,
        // this is important because we don't want to load every class referenced within!
        loader.modmenuFactoryPaths.addAll(modmenuFactoryPaths);
        // After all the stubs have been defined the class loader is ready to load classes
        return loader;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // If the class is a stub, we define it in our loader using our stub data
        if (stubs.containsKey(name))
            return this.defineClass(name, stubs.get(name), 0, stubs.get(name).length);
        // If the class is a modmenu implementing class, we load it using our class loader.
        // This way the class uses our stubs, instead of looking for the real classes
        if (this.modmenuFactoryPaths.contains(name)) {
            try {
                byte[] original = this.readClassBytes(name);
                return this.defineClass(name, original, 0, original.length);
            } catch (IOException e) {
                throw new ClassNotFoundException("Failed to transform: " + name, e);
            }
        }
        // If we don't need to load the class using our loader, we throw an
        // exception, so loading gets delegated to the parent by "loadClass"
        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (this.getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> loaded = this.findLoadedClass(name);
            if (loaded != null) {
                if (resolve) this.resolveClass(loaded);
                return loaded;
            }
            // If the class isn't loaded, we attempt to define it using our loader
            // This way if we run into classes we need to "swap-out" we can do so
            try {
                Class<?> clazz = this.findClass(name);
                if (resolve) this.resolveClass(clazz);
                return clazz;
            } catch (ClassNotFoundException ignored) {}
            // Lastly we delegate to the parent class loader for everything else
            // Which: (is already loaded) > (try to load/find it) > (resolve if needed)
            return super.loadClass(name, resolve);
        }
    }

    private byte[] readClassBytes(String name) throws IOException {
        String path = name.replace('.', '/') + ".class";
        try (InputStream is = this.getParent().getResourceAsStream(path)) {
            if (is == null)
                throw new IOException("Class resource not found: " + path);
            return is.readAllBytes();
        }
    }
}