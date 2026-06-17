package com.github.andrew0030.pandora_core.modules.instancer.mixin.engine;

import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(Level.class)
public class EngineAttacher implements PacoInstancingLevel {
    @Unique
    private InstanceManager pandoraCore$manager;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void postInit(WritableLevelData levelData, ResourceKey dimension, RegistryAccess registryAccess, Holder dimensionTypeRegistration, Supplier profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates, CallbackInfo ci) {
        //noinspection ConstantValue
        if (!(((Object) this) instanceof ServerLevel)) {
            pandoraCore$manager = new InstanceManager();
        } else {
            pandoraCore$manager = null;
        }
    }

    @Override
    public InstanceManager getManager() {
        return pandoraCore$manager;
    }
	
	@Override
	public @Nullable Level getLevel() {
		return (Level) (Object) this;
	}
}
