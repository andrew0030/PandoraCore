package com.github.andrew0030.pandora_core.mixin.world;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoForgeModifierConverter;
import com.github.andrew0030.pandora_core.registry.internal.PaCoRegistryKeys;
import com.github.andrew0030.pandora_core.world.Modifier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ServerLifecycleHooks.class, remap = false)
public class ServerLifeCycleHooksMixin {

    /*
     * Since Forge works with ModifiableBiomeInfo instead of the Biome itself there is two options:
     * - Apply all PaCo modifiers before Forge applies its Modifiers, so it copies the BiomeInfo with the PaCo modifiers applied.
     * - Convert the PaCo modifiers into Forge modifiers (when possible).
     *
     * The issue with running them before Forge's is that it results in cross system incompatibility. This isn't the end
     * of the world, but it's not ideal. So instead I went with a self-mixin approach that adds a converter functional interface.
     * The converter is then called when Forge loads and stores its modifiers, and it adds them to the list.
     * Any PaCo modifiers that don't implement the converter (modifiers that don't have a forge equivalent), are simply applied.
     */
    @ModifyVariable(method = "runModifiers", at = @At(value = "STORE", ordinal = 0))
    private static List<BiomeModifier> modifyBiomeModifiers(List<BiomeModifier> biomeModifiers, MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        List<BiomeModifier> toAdd = new ArrayList<>();

        registryAccess.lookupOrThrow(PaCoRegistryKeys.WORLDGEN_MODIFIER).listElements().forEach(modifierReference -> {
            Modifier modifier = modifierReference.value();
            if (modifier instanceof IPaCoForgeModifierConverter converter) {
                toAdd.add(converter.convertToForgeModifier());
            } else {
                modifier.applyModifier();
            }
        });

        List<BiomeModifier> merged = new ArrayList<>(biomeModifiers.size() + toAdd.size());
        merged.addAll(biomeModifiers);
        merged.addAll(toAdd);
        return merged;
    }
}