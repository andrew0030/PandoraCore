package com.github.andrew0030.pandora_core.mixin.player;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoPlayerSideCheck;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class PlayerMixin implements IPaCoPlayerSideCheck {

    @Override
    public boolean pandoraCore$isServerPlayer() {
        return false;
    }
}