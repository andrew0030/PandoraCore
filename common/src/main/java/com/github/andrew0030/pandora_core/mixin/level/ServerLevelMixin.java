package com.github.andrew0030.pandora_core.mixin.level;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoLevelSideCheck;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements IPaCoLevelSideCheck {

    @Override
    public boolean pandoraCore$isServerSide() {
        return true;
    }
}