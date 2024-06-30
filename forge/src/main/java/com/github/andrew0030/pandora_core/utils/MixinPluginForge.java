package com.github.andrew0030.pandora_core.utils;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.InputStream;
import java.util.*;

public class MixinPluginForge implements IMixinConfigPlugin
{
    private static final ArrayList<String> classLookup = new ArrayList<>();
    private static final ArrayList<String> pkgLookup = new ArrayList<>();
    private static final HashMap<String, ArrayList<String>> incompatibilityMap = new HashMap<>();

    /** Only applies the given Mixin, if the Class it targets exists. */
    private static void addClassLookup(String mixin) {
        classLookup.add(mixin);
    }

    /**
     * Only applies Mixins in the given Package, if the Class they target exists.<br/>
     * Essentially the Package based bulk operation of using {@link com.github.andrew0030.pandora_core.utils.MixinPluginForge#addClassLookup(String)}
     */
    private static void addPkgLookup(String mixin) {
        pkgLookup.add(mixin);
    }

    /** Doesn't load the given Mixin, if one of the given classes exists. */
    private static void addIncompat(String mixin, String... things) {
        ArrayList<String> incompat = new ArrayList<>(Arrays.asList(things));
        incompatibilityMap.put(mixin, incompat);
    }

    static {
        addPkgLookup("com.github.andrew0030.pandora_core.mixin.compat");
//        addClassLookup("com.github.andrew0030.pandora_core.mixin.compat.gui.CatalogueModListScreenMixin");
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    public boolean doesPkgNeedLookup(String name) {
        for (String s : pkgLookup) {
            if (name.startsWith(s)) return true;
        }
        return false;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (classLookup.contains(mixinClassName) || doesPkgNeedLookup(mixinClassName)) {
            ClassLoader loader = MixinPluginForge.class.getClassLoader();
            // tests if the classloader contains a .class file for the target
            InputStream stream = loader.getResourceAsStream(targetClassName.replace('.', '/') + ".class");
            if (stream != null) {
                try {
                    stream.close();
                    return true;
                } catch (Throwable ignored) {
                    return true;
                }
            }
            return false;
        }

        if (incompatibilityMap.containsKey(mixinClassName)) {
            ClassLoader loader = MixinPluginForge.class.getClassLoader();
            // tests if the classloader contains a .class file for the target
            for (String name : incompatibilityMap.get(mixinClassName)) {
                InputStream stream = loader.getResourceAsStream(name.replace('.', '/') + ".class");
                if (stream == null) {
                    return true;
                } else {
                    try {
                        stream.close();
                        return false;
                    } catch (Throwable ignored) {
                        return false;
                    }
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}