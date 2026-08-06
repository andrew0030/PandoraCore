package com.github.andrew0030.pandora_core.config.manager;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.ConfigEntryFactory;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.UnsupportedEntry;
import net.minecraft.util.StringUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class ConfigDataHolder {
    protected String path = "";
    protected String comment;
    protected int commentPadding = 1;
    protected ConfigEntryFactory configEntryFactory;

    public ConfigDataHolder setPath(String path) {
        this.path = path;
        return this;
    }

    public String getPath() {
        return this.path;
    }

    public ConfigDataHolder setComment(String comment) {
        this.setComment(comment, 1);
        return this;
    }

    public ConfigDataHolder setComment(String comment, int padding) {
        this.comment = comment;
        this.commentPadding = padding;
        return this;
    }

    /** @return the config holder comment as is, meaning this doesn't contain "padding" */
    public String getCommentRaw() {
        if (this.comment != null)
            return this.comment;
        return "";
    }

    /** @return the config holder comment with padding */
    public String getComment() {
        return Arrays.stream(this.getCommentRaw().split("\n"))
                .map(line -> " ".repeat(Math.max(0, this.commentPadding)) + line.trim())
                .collect(Collectors.joining("\n"));
    }

    /** @return whether the config holder has a comment */
    public boolean hasComment() {
        return !StringUtil.isNullOrEmpty(this.getCommentRaw());
    }




    // TODO write javadocs
    public ConfigDataHolder setConfigEntryFactory(ConfigEntryFactory factory) {
        this.configEntryFactory = factory;
        return this;
    }

    // TODO write javadocs
    public ConfigEntryFactory getConfigEntryFactory() {
        // Fallback just in case, though AnnotationHandler should always set it
        return this.configEntryFactory != null ? this.configEntryFactory : UnsupportedEntry::new;
    }

    public boolean hasField() {
        return false;
    }
}