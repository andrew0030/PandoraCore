package com.github.andrew0030.pandora_core.mixin.self.world;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoForgeModifierConverter;
import com.github.andrew0030.pandora_core.world.modifier.AddFeaturesModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AddFeaturesModifier.class)
public class AddFeaturesModifierSelfMixin implements IPaCoForgeModifierConverter {

    @Override
    public BiomeModifier convertToForgeModifier() {
        AddFeaturesModifier modifier = (AddFeaturesModifier) (Object) this;
        return new ForgeBiomeModifiers.AddFeaturesBiomeModifier(modifier.biomes(), modifier.features(), modifier.step());
    }
}