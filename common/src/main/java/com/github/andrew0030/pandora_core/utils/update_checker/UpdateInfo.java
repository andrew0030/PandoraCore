package com.github.andrew0030.pandora_core.utils.update_checker;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public class UpdateInfo {
    private final Status status;
    private final Source source;
    private final Type type;
    private final String newVersion;
    private final URL downloadURL;

    public UpdateInfo(@NotNull Status status, @NotNull Source source, @Nullable Type type, @Nullable String newVersion, @Nullable URL downloadURL) {
        this.status = status;
        this.source = source;
        this.type = type;
        this.newVersion = newVersion;
        this.downloadURL = downloadURL;
    }

    public Status getStatus() {
        return this.status;
    }

    public Source getSource() {
        return this.source;
    }

    @Nullable
    public Type getType() {
        return this.type;
    }

    @Nullable
    public String getNewVersion() {
        return this.newVersion;
    }

    @Nullable
    public URL getDownloadURL() {
        return this.downloadURL;
    }

    public enum Status {
        PENDING(false),
        FAILED(false),
        UP_TO_DATE(false),
        OUTDATED(true),
        AHEAD(false),
        BETA(false),
        BETA_OUTDATED(true),
        NO_PROMOS(false);

        private final boolean outdated;

        Status(boolean outdated) {
            this.outdated = outdated;
        }

        public boolean isOutdated() {
            return this.outdated;
        }
    }

    public enum Source {
        URL,
        MODRINTH;
    }

    public enum Type {
        RECOMMENDED("gui.pandora_core.paco.content.mod.update.type.recommended"),
        LATEST("gui.pandora_core.paco.content.mod.update.type.latest"),
        ALPHA("gui.pandora_core.paco.content.mod.update.type.alpha"),
        BETA("gui.pandora_core.paco.content.mod.update.type.beta"),
        RELEASE("gui.pandora_core.paco.content.mod.update.type.release");

        private final Component displayName;

        Type(String name) {
            this.displayName = Component.translatable(name);
        }

        public Component getDisplayName() {
            return this.displayName;
        }
    }
}