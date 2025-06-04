package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.key.PaCoKeyMappingRegistry;
import com.github.andrew0030.pandora_core.network.PacketTarget;
import com.github.andrew0030.pandora_core.test.networking.PaCoNetworking;
import com.github.andrew0030.pandora_core.test.networking.packet.c2s.KeyPressedPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class PaCoKeyMappings {
    public static final PaCoKeyMappingRegistry KEY_MAPPINGS = new PaCoKeyMappingRegistry();

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
            KeyPressedPacket packet = new KeyPressedPacket(GLFW.GLFW_KEY_J);
            PaCoNetworking.NETWORK_INSTANCE.send(PacketTarget.sendToServer(), packet);
        }
    );
}