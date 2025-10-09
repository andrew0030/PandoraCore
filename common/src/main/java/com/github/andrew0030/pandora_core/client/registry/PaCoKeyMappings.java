package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.client.PaCoClientTicker;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.key.PaCoKeyMappingRegistry;
import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShakeManager;
import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.curve_shake.CurveScreenShake;
import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.enums.HorizontalDirection;
import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.manual_shake.ManualScreenShake;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class PaCoKeyMappings {
    public static final PaCoKeyMappingRegistry KEY_MAPPINGS = new PaCoKeyMappingRegistry();

    public static final ManualScreenShake manualShake = new ManualScreenShake();
    static {
        ScreenShakeManager.addScreenShake(manualShake);
    }

    public static final KeyMapping KEY_PACO = KEY_MAPPINGS.add(
        new KeyMapping(
            "key.pandora_core.pandora_core",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            "category.pandora_core.pandora_core"
        ), () -> {
//        ScreenShakeManager.addScreenShake(new CurveScreenShake(10)
//                .setYaw(YawDirection.RANDOM, 5F, 3).setYawEasing(EasingDirection.OUT, Easing.LINEAR)
//                .setPitch(PitchDirection.DOWN, 7F, 6).setPitchEasing(EasingDirection.OUT, Easing.LINEAR)
//                .setRoll(RollDirection.RANDOM, 3F, 10).setRollEasing(EasingDirection.OUT, Easing.LINEAR)
//        );
            Minecraft.getInstance().setScreen(new PaCoScreen());
        }
    );

    public static final KeyMapping KEY_TEST = KEY_MAPPINGS.add(
        new KeyMapping(
            "Test",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "category.pandora_core.pandora_core"
        ), () -> {
//            KeyPressedPacket packet = new KeyPressedPacket(GLFW.GLFW_KEY_J);
//            PaCoNetworking.CHANNEL.send(PacketTarget.sendToServer(), packet);
            ScreenShakeManager.addScreenShake(new CurveScreenShake(20, true)
                // Rotation
//                .setPitch(PitchDirection.DOWN, 10, 10)
//                .setYaw(YawDirection.LEFT, 60, 1)
//                .setPitch(PitchDirection.UP, 60, 1)
//                .setRoll(RollDirection.RIGHT, 30, 1)
                // Position Relative
                .setHorizontal(HorizontalDirection.RIGHT, 2, 1)
//                .setVertical(VerticalDirection.UP, 2, 1)
//                .setDepth(DepthDirection.FORWARDS, 2, 1)
                // Position Absolute
//                .setX(AxisDirection.POSITIVE, 1, 1)
//                .setY(AxisDirection.POSITIVE, 1, 1)
//                .setZ(AxisDirection.POSITIVE, 2, 1)
                // Field of View
//                .setFov(FovDirection.FORWARDS, 10, 1)
            );
        }
    );

    public static final KeyMapping KEY_TEST_2 = KEY_MAPPINGS.add(
        new KeyMapping(
            "Test 2",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.pandora_core.pandora_core"
        ), () -> {
//            manualShake.setYaw(manualShake.getYawOffset(PaCoClientTicker.getPartialTick()) + 20);

//            float offset = manualShake.getYOffset(PaCoClientTicker.getPartialTick());
//            manualShake.setYOffset(offset != 0.0F ? offset - 1 : offset + 1);

            float offset = manualShake.getZOffset(PaCoClientTicker.getPartialTick());
            manualShake.setZOffset(offset != 0.0F ? offset - 2 : offset + 2);

//            float pos = manualShake.getHorizontalOffset(PaCoClientTicker.getPartialTick());
//            float rot = manualShake.getYawOffset(PaCoClientTicker.getPartialTick());
//            manualShake.setHorizontalOffset(pos != 0.0F ? pos + 1 : pos - 1);
//            manualShake.setYaw(rot != 0.0F ? rot + 30 : rot - 30);
        }
    );
}