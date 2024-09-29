package com.github.andrew0030.pandora_core.utils.shader_checker.loaders;

import com.github.andrew0030.pandora_core.platform.Services;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;

public class IrisShaderHandler extends BaseShaderHandler {

    @Override
    protected boolean detectMod() {
        return Services.PLATFORM.isModLoaded("iris") || Services.PLATFORM.isModLoaded("oculus");
    }

    @Override
    protected void initMethodHandle() {
        try {
            MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
            MethodType mt = MethodType.methodType(Optional.class);
            boolean usesNewPath = true;

            // Detect the correct class path
            try {
                Class<?> clazz = Class.forName("net.coderbot.iris.Iris");
                if (clazz != null) usesNewPath = false;
            } catch (Throwable ignored) {}

            String classPath = usesNewPath ? "net.irisshaders.iris.Iris" : "net.coderbot.iris.Iris";
            this.handle = publicLookup.findStatic(Class.forName(classPath), "getCurrentPack", mt);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isShaderLoaded() {
        if (this.handle != null) {
            try {
                return ((Optional<?>) this.handle.invoke()).isPresent();
            } catch (Throwable ignored) {}
        }
        return false;
    }
}