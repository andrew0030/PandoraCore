package com.github.andrew0030.pandora_core.mixin.compat.instancing;

import com.github.andrew0030.pandora_core.client.render.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.client.render.instancing.engine.PacoInstancingLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.WritableLevelData;
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
    public void postInit(WritableLevelData $$0, ResourceKey $$1, RegistryAccess $$2, Holder $$3, Supplier $$4, boolean $$5, boolean $$6, long $$7, int $$8, CallbackInfo ci) {
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
}
