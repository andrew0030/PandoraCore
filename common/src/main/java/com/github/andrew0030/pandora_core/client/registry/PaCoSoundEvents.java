package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.registry.PaCoRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class PaCoSoundEvents {
    public static final PaCoRegistry<SoundEvent> SOUND_EVENTS = new PaCoRegistry<>(BuiltInRegistries.SOUND_EVENT, PandoraCore.MOD_ID);

    public static final Supplier<SoundEvent> DUN_DUN_DUUUN = SOUND_EVENTS.add("dun_dun_duuun", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(PandoraCore.MOD_ID, "dun_dun_duuun")));
}