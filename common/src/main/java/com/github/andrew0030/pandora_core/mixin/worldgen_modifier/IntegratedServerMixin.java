package com.github.andrew0030.pandora_core.mixin.worldgen_modifier;

import com.github.andrew0030.pandora_core.world.Modifier;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public final class IntegratedServerMixin {
    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;loadLevel()V"))
    private void injectInitServer(CallbackInfoReturnable<Boolean> info) {
        Modifier.applyAll((MinecraftServer) (Object) this);
    }
}