package com.github.andrew0030.pandora_core.utils.shader_checker.loaders;

import org.jetbrains.annotations.ApiStatus;

import javax.annotation.CheckForNull;
import java.lang.invoke.MethodHandle;

public abstract class BaseShaderHandler {
    private Boolean isLoaded;
    @CheckForNull
    protected MethodHandle handle;

    /** Each subclass should define how to check if the mod is present */
    @ApiStatus.OverrideOnly
    protected abstract boolean detectMod();

    /** Each subclass should initialize its method handle */
    @ApiStatus.OverrideOnly
    protected abstract void initMethodHandle();

    /** @return Whether the mod is loaded */
    public boolean isLoaded() {
        // Lazy initialization of mod detection
        if (this.isLoaded == null) {
            if (this.detectMod()) {
                this.isLoaded = true;
                // Initialize the method handle lazily
                this.initMethodHandle();
            } else {
                this.isLoaded = false;
            }
        }
        return this.isLoaded;
    }

    /**
     * Each subclass should check if shaders are active using the {@link BaseShaderHandler#handle},
     * initialized by {@link BaseShaderHandler#initMethodHandle()}.<br/>
     * Note: Make sure to check if {@link BaseShaderHandler#handle} is <strong>null</strong> as it's
     * only initialized of the mod is actually loaded.
     */
    public abstract boolean isShaderLoaded();
}