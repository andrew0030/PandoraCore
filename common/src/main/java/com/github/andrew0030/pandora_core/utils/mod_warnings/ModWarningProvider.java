package com.github.andrew0030.pandora_core.utils.mod_warnings;

import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * This interface should be implemented to create ModWarningFactory classes.<br/>
 * <strong>Note</strong>: this code gets called by both the <strong>client</strong> and <strong>server</strong>,
 * so avoid client only logic, or it will crash servers.
 */
@FunctionalInterface
public interface ModWarningProvider {
    List<Component> getWarnings();
}