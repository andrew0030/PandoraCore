package com.github.andrew0030.pandora_core.utils.update_checker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

//TODO:
// - Add version type (release | beta | alpha)
// - Add latest version (to show a message like 1.2.0 -> 1.4.0)
public class UpdateInfo {
    private final Status status;
    private final Source source;
    private final URL downloadURL;

    public UpdateInfo(@NotNull Status status, @NotNull Source source, @Nullable URL downloadURL) {
        this.status = status;
        this.source = source;
        this.downloadURL = downloadURL;
    }

    public Status getStatus() {
        return this.status;
    }

    public Source getSource() {
        return this.source;
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