package com.github.andrew0030.pandora_core.mixin.level;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoLevelSideCheck;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public class LevelMixin implements IPaCoLevelSideCheck {

    @Override
    public boolean pandoraCore$isServerSide() {
        return false;
    }
}