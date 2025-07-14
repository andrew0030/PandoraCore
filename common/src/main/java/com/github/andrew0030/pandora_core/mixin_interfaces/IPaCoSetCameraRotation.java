package com.github.andrew0030.pandora_core.mixin_interfaces;

public interface IPaCoSetCameraRotation {
    void pandoraCore$setRotation(float xRot, float yRot, float zRot, float partialTick);
    void pandoraCore$setPositionRelative(float xPos, float yPos, float zPos);
    void pandoraCore$setPositionAbsolute(float xPos, float yPos, float zPos);
    float pandoraCore$getZRot();
}