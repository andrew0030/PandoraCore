package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.client.key.PaCoKeyMappings;
import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShakeManager;
import com.github.andrew0030.pandora_core.client.screen_shaker.curve_shake.*;
import com.github.andrew0030.pandora_core.events.FabricClientTickEvent;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class PandoraCoreClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Common Module Initialization.
        PandoraCoreClient.init();
        PandoraCoreClient.initThreadSafe();

        // Loader Module Initialization.
        PandoraCoreClientFabric.registerBindings();
        PandoraCoreClientFabric.handleKeyMappings();
        FabricClientTickEvent.init();
    }

    private static void registerBindings() {
        for (KeyMapping keyMapping : PaCoKeyMappings.KEY_MAPPINGS) {
            KeyBindingHelper.registerKeyBinding(keyMapping);
        }
    }

    private static void handleKeyMappings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (PaCoKeyMappings.KEY_PACO.consumeClick()) {
//                Minecraft.getInstance().setScreen(new PaCoScreen());
//                ScreenShakeManager.addScreenShake(new CurveScreenShake(20).setYaw(12F, 3).setPitch(-14F, 6).setRoll(16F, 10));

//                ScreenShakeManager.addScreenShake(new CurveScreenShake(10)
//                        .setYaw(5F, 3)
//                        .setPitch(7F, 6)
//                        .setRoll(3F, 10)
//                );

                ScreenShakeManager.addScreenShake(new CurveScreenShake(10)
                        .setYaw(YawDirection.RANDOM, 5F, 3).setYawEasing(EasingDirection.OUT, Easing.LINEAR)
                        .setPitch(PitchDirection.DOWN, 7F, 6).setPitchEasing(EasingDirection.OUT, Easing.LINEAR)
                        .setRoll(RollDirection.RANDOM, 3F, 10).setRollEasing(EasingDirection.OUT, Easing.LINEAR)
                );

//                ScreenShakeManager.addScreenShake(new CurveScreenShake(100)
//                        .setYaw(YawDirection.RANDOM, 60F, 5).setYawEasing(EasingDirection.OUT, Easing.STEPS.setIntValue(18))
//                        .setPitch(PitchDirection.RANDOM, 40F, 8).setPitchEasing(EasingDirection.OUT, Easing.STEPS.setIntValue(18))
//                        .setRoll(RollDirection.RANDOM, 10F, 6).setRollEasing(EasingDirection.OUT, Easing.STEPS.setIntValue(18))
//                );
            }
        });
    }
}