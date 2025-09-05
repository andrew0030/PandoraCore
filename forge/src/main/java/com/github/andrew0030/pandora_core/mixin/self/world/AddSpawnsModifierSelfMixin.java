package com.github.andrew0030.pandora_core.mixin.self.world;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoForgeModifierConverter;
import com.github.andrew0030.pandora_core.world.modifier.AddSpawnsModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AddSpawnsModifier.class)
public class AddSpawnsModifierSelfMixin implements IPaCoForgeModifierConverter {

    @Override
    public BiomeModifier convertToForgeModifier() {
        AddSpawnsModifier modifier = (AddSpawnsModifier) (Object) this;
        return new ForgeBiomeModifiers.AddSpawnsBiomeModifier(modifier.biomes(), modifier.spawners());
    }
}