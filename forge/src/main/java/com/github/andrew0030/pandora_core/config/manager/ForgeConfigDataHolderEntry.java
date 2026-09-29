package com.github.andrew0030.pandora_core.config.manager;

import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ForgeConfigDataHolderEntry<T> extends ConfigDataHolder<T> implements IConfigValueHolder<T> {
    private final ForgeConfigSpec.ConfigValue<T> value;
    private final ForgeConfigSpec.ValueSpec spec; // TODO remove this eventually as its no longer needed

    private T minVal;
    private T maxVal;
    private boolean showFullRange;

    public ForgeConfigDataHolderEntry(ForgeConfigSpec.ConfigValue<T> value, ForgeConfigSpec.ValueSpec spec) {
        this.value = value;
        this.spec = spec;
    }

    @Override
    public void setValue(T value) {
        this.value.set(value);
    }

    @Override
    public T getValue() {
        return this.value.get();
    }

    @Override
    public T getMinVal() {
        return this.minVal;
    }

    @Override
    public T getMaxVal() {
        return this.maxVal;
    }

    /** Used to cache the value range (if applicable), which is then used for internal logic */
    @ApiStatus.Internal
    public ForgeConfigDataHolderEntry<T> setRange(@Nullable T minVal, @Nullable T maxVal) {
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
    public ForgeConfigDataHolderEntry<T> setShowFullRange(boolean showFullRange, @NotNull T minVal, @NotNull T maxVal) {
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