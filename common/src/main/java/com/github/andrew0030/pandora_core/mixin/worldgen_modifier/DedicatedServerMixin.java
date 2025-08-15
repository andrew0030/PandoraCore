package com.github.andrew0030.pandora_core.mixin.worldgen_modifier;

import com.github.andrew0030.pandora_core.world.Modifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;loadLevel()V"))
    public void injectInitServer(CallbackInfoReturnable<Boolean> info) {
        Modifier.applyAll((MinecraftServer) (Object) this);
    }
}