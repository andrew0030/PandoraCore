package com.github.andrew0030.pandora_core.mixin_interfaces;

//TODO rename this interface and adjust the methods
public interface IPaCoSetCameraRotation {
    void pandoraCore$setRotation(float xRot, float yRot, float zRot, float partialTick);
    void pandoraCore$setPositionRelative(float horizontalOffset, float verticalOffset, float depthOffset);
    void pandoraCore$setPositionAbsolute(float xPos, float yPos, float zPos);
    void pandoraCore$setFOVOffset(float fovOffset, float partialTick);
    float pandoraCore$getZRot();
    float pandoraCore$getFOVOffset();
}