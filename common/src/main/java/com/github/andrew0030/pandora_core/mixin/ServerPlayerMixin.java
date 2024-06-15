package com.github.andrew0030.pandora_core.mixin;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoPlayerSideCheck;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IPaCoPlayerSideCheck {

    @Override
    public boolean pandoraCore$isServerPlayer() {
        return true;
    }
}