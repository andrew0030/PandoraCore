package com.github.andrew0030.pandora_core.mixin_interfaces;

public interface IPaCoCameraTransforms {
    void pandoraCore$setRotation(float pitchOffset, float yawOffset, float rollOffset, float partialTick);
    void pandoraCore$setPositionRelative(float horizontalOffset, float verticalOffset, float depthOffset);
    void pandoraCore$setPositionAbsolute(float xOffset, float yOffset, float zOffset);
    void pandoraCore$setFovOffset(float fovOffset, float partialTick);
    float pandoraCore$getZRot();
    float pandoraCore$getFovOffset();
}