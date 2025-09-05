package com.github.andrew0030.pandora_core.mixin.self.world;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoForgeModifierConverter;
import com.github.andrew0030.pandora_core.world.modifier.RemoveSpawnsModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RemoveSpawnsModifier.class)
public class RemoveSpawnsModifierSelfMixin implements IPaCoForgeModifierConverter {

    @Override
    public BiomeModifier convertToForgeModifier() {
        RemoveSpawnsModifier modifier = (RemoveSpawnsModifier) (Object) this;
        return new ForgeBiomeModifiers.RemoveSpawnsBiomeModifier(modifier.biomes(), modifier.entityTypes());
    }
}