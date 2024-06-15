package com.github.andrew0030.pandora_core.utils;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoLevelSideCheck;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoPlayerSideCheck;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Helper Class that allows checking Sides without <b>instanceof</b> checks. */
public class SideChecker {

    /**
     * @param level The {@link Level} to check.
     * @return Whether the given {@link Level} is a {@link ServerLevel}
     */
    public static boolean isServerLevel(Level level) {
        return ((IPaCoLevelSideCheck) level).pandoraCore$isServerSide();
    }

    /**
     * @param player The {@link Player} to check.
     * @return Whether the given {@link Player} is a {@link ServerPlayer}
     */
    public static boolean isServerPlayer(Player player) {
        return ((IPaCoPlayerSideCheck) player).pandoraCore$isServerPlayer();
    }
}