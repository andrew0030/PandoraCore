package com.github.andrew0030.pandora_core.mixin_interfaces;

public interface IPaCoSetCameraRotation {
    void pandoraCore$setRotation(float xRot, float yRot, float zRot, float partialTick);
    float pandoraCore$getXRot();
    float pandoraCore$getYRot();
    float pandoraCore$getZRot();
}