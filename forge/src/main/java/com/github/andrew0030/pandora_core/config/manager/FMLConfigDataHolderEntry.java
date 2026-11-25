package com.github.andrew0030.pandora_core.config.manager;

import com.github.andrew0030.pandora_core.config.access.FMLConfigAccess;
import net.minecraftforge.fml.loading.FMLConfig;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// TODO probably return null for ranges as FMLConfig doesn't have any ranges
public class FMLConfigDataHolderEntry<T> extends ConfigDataHolder<T> implements IConfigValueHolder<T> {
    private final FMLConfig.ConfigValue value;
    private List<String> validValues;
    private T minVal;
    private T maxVal;
    private boolean showFullRange;

    public FMLConfigDataHolderEntry(FMLConfig.ConfigValue value) {
        this.value = value;
    }

    @Override
    public void setValue(T value) {
        FMLConfig.updateConfig(this.value, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getValue() {
        return (T) FMLConfigAccess.getRawValue(this.path);
    }

    @Override
    public List<String> getValidValues() {
        return this.validValues;
    }

    @Override
    public T getMinVal() {
        return this.minVal;
    }

    @Override
    public T getMaxVal() {
        return this.maxVal;
    }

    public FMLConfigDataHolderEntry<T> setValidValues(List<String> validValues) {
        this.validValues = validValues;
        return this;
    }

    @ApiStatus.Internal
    public FMLConfigDataHolderEntry<T> setRange(@Nullable T minVal, @Nullable T maxVal) {
        if (this.minVal == null)
            this.minVal = minVal;
        if (this.maxVal == null)
            this.maxVal = maxVal;
        return this;
    }

    @ApiStatus.Internal
    public FMLConfigDataHolderEntry<T> setShowFullRange(boolean showFullRange, @NotNull T minVal, @NotNull T maxVal) {
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