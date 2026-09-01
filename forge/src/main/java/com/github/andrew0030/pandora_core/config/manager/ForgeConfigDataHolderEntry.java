package com.github.andrew0030.pandora_core.config.manager;

import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ForgeConfigDataHolderEntry<T> extends ConfigDataHolder<T> implements IConfigValueHolder<T> {
    private final ForgeConfigSpec.ConfigValue<T> value;
    private final ForgeConfigSpec.ValueSpec spec;

    private Number minVal;
    private Number maxVal;
    private boolean showFullRange;

    public ForgeConfigDataHolderEntry(ForgeConfigSpec.ConfigValue<T> value, ForgeConfigSpec.ValueSpec spec) {
        this.value = value;
        this.spec = spec;
    }

    @Override
    public void setValue(T value) {
        // TODO: Push this to a staging/pending changes map instead of applying immediately!
        this.value.set(value);
    }

    @Override
    public T getValue() {
        return this.value.get();
    }

    /** Used to cache the value range (if applicable), which is then used for internal logic */
    @ApiStatus.Internal
    public ForgeConfigDataHolderEntry<T> setRange(@Nullable Number minVal, @Nullable Number maxVal) {
        // We check for null to make sure this won't override "showFullRange", this is technically a bit
        // overkill as both of these methods are flagged as internal, however I say "better safe than sorry!"
        if (this.minVal == null)
            this.minVal = minVal;
        if (this.maxVal == null)
            this.maxVal = maxVal;
        return this;
    }

    // TODO: I may not even need this since I don't really show ranges directly in the UI
    /** Used to toggle whether the range should be displayed, regardless of the value. (Useful for small values like byte) */
    @ApiStatus.Internal
    public ForgeConfigDataHolderEntry<T> setShowFullRange(boolean showFullRange, @NotNull Number minVal, @NotNull Number maxVal) {
        this.showFullRange = showFullRange;
        if (showFullRange) {
            this.minVal = minVal;
            this.maxVal = maxVal;
        }
        return this;
    }

    @Override
    public boolean hasValue() {
        return true;
    }
}