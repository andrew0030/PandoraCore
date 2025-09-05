package com.github.andrew0030.pandora_core.mixin.world;

import com.github.andrew0030.pandora_core.world.modifier.Modifier;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameTestServer.class)
public class GameTestServerMixin {

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/gametest/framework/GameTestServer;loadLevel()V"))
    public void injectInitServer(CallbackInfoReturnable<Boolean> info) {
        Modifier.applyAll((MinecraftServer) (Object) this);
    }
}