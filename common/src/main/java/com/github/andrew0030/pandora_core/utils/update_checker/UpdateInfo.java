package com.github.andrew0030.pandora_core.utils.update_checker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public class UpdateInfo {
    private final Status status;
    private final Source source;
    private final String changelog;
    private final URL downloadURL;

    public UpdateInfo(@NotNull Status status, @NotNull Source source, @Nullable String changelog, @Nullable URL downloadURL) {
        this.status = status;
        this.source = source;
        this.changelog = changelog;
        this.downloadURL = downloadURL;
    }

    public Status getStatus() {
        return this.status;
    }

    public Source getSource() {
        return this.source;
    }

    @Nullable
    public String getChangelog() {
        return this.changelog;
    }

    @Nullable
    public URL getDownloadURL() {
        return this.downloadURL;
    }

    public enum Source {
        URL, MODRINTH;
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
}