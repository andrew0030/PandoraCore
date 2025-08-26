package com.github.andrew0030.pandora_core.mixin.self.world;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoForgeModifierConverter;
import com.github.andrew0030.pandora_core.world.RemoveFeaturesModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RemoveFeaturesModifier.class)
public class RemoveFeaturesModifierSelfMixin implements IPaCoForgeModifierConverter {

    @Override
    public BiomeModifier convertToForgeModifier() {
        RemoveFeaturesModifier modifier = (RemoveFeaturesModifier) (Object) this;
        return new ForgeBiomeModifiers.RemoveFeaturesBiomeModifier(modifier.biomes(), modifier.features(), modifier.steps());
    }
}