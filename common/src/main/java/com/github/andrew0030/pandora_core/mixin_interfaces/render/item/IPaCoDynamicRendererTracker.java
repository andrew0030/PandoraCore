package com.github.andrew0030.pandora_core.mixin_interfaces.render.item;

public interface IPaCoDynamicRendererTracker {
    boolean pandoraCore$isDynamicRendering();
    void pandoraCore$setDynamicRendering(boolean value);
}