package com.github.andrew0030.pandora_core.config.annotation.annotations;

// TODO once networking is set up, make sure to sync configs on server join/change if needed
// TODO maybe remove this entirely and make more obvious config annotations for syncing and or "server" (per-world) configs.
public enum ConfigType {
    CLIENT,
    COMMON,
    SERVER
}