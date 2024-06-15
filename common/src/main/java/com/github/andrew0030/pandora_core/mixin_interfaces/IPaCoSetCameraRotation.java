package com.github.andrew0030.pandora_core.mixin_interfaces;

public interface IPaCoSetCameraRotation {
    void pandoraCore$setRotation(float yaw, float pitch, float roll);
    float pandoraCore$getZRot();
}