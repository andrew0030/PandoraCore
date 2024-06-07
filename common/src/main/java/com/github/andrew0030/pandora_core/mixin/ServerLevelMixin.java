package com.github.andrew0030.pandora_core.mixin;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoLevelSideCheck;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements IPaCoLevelSideCheck {

    @Override
    public boolean isPaCoServerSide() {
        return true;
    }
}