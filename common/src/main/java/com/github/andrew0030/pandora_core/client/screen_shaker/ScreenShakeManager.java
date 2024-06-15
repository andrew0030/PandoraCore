package com.github.andrew0030.pandora_core.client.screen_shaker;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoSetCameraRotation;
import net.minecraft.client.Camera;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScreenShakeManager {
    public static final List<ScreenShake> ACTIVE_SHAKES = new ArrayList<>();
    public static float yawOffset;
    public static float pitchOffset;
    public static float rollOffset;

    public static void updateCamera(Camera camera, float partialTick) {
        yawOffset = 0.0F;
        pitchOffset = 0.0F;
        rollOffset = 0.0F;

        for (ScreenShake shake : ACTIVE_SHAKES) {
            yawOffset += shake.getYawOffset(partialTick);
            pitchOffset += shake.getPitchOffset(partialTick);
            rollOffset += shake.getRollOffset(partialTick);
        }

        ((IPaCoSetCameraRotation) camera).pandoraCore$setRotation(camera.getYRot() + yawOffset, camera.getXRot() + pitchOffset, rollOffset);
    }

    public static void tickCameraShakes() {
        Iterator<ScreenShake> iterator = ACTIVE_SHAKES.iterator();
        while (iterator.hasNext()) {
            ScreenShake shake = iterator.next();
            shake.update();
            if (shake.isFinished()) {
                iterator.remove();
            }
        }
    }

    public static void addScreenShake(ScreenShake shake) {
        ACTIVE_SHAKES.add(shake);
    }
}