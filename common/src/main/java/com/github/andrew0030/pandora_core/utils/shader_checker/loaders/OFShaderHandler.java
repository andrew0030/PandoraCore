package com.github.andrew0030.pandora_core.utils.shader_checker.loaders;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class OFShaderHandler extends BaseShaderHandler {

    @Override
    protected boolean detectMod() {
        try {
            Class<?> clazz = Class.forName("net.optifine.Config");
            return clazz != null;
        } catch (Throwable ignored) {}
        return false;
    }

    @Override
    protected void initMethodHandle() {
        try {
            MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
            MethodType mt = MethodType.methodType(boolean.class);
            this.handle = publicLookup.findStatic(Class.forName("net.optifine.Config"), "isShaders", mt);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isShaderLoaded() {
        if (this.handle != null) {
            try {
                return (boolean) this.handle.invoke();
            } catch (Throwable ignored) {}
        }
        return false;
    }
}