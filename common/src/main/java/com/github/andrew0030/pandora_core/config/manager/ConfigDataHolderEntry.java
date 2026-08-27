package com.github.andrew0030.pandora_core.config.manager;

import com.github.andrew0030.pandora_core.config.PaCoMainConfig;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

public class ConfigDataHolderEntry<T> extends ConfigDataHolder<T> {
    private final Field field;
    private IPaCoConfigConverter<T, ?> converter;
    private List<String> validValues;
    private Number minVal;
    private Number maxVal;
    private boolean showFullRange;

    public ConfigDataHolderEntry(Field field) {
        this.field = field;
    }

    public Field getField() {
        return this.field;
    }

    public String getFieldName() {
        return this.field.getName();
    }

    public ConfigDataHolderEntry<T> setConverter(IPaCoConfigConverter<T, ?> converter) {
        this.converter = converter;
        return this;
    }

    /**
     * Serializes a runtime {@link Object} into the corresponding config file value.
     * <p>
     * If an {@link IPaCoConfigConverter} is present, it is called on the value before it is written to
     * the config file, as the type may require a conversion, e.g. (Enum -> String) or (Float -> Double).
     *
     * @param value The deserialized runtime {@link Object} to be serialized
     * @return The serialized representation of this object ready to be written to the config file.
     *         If no {@link IPaCoConfigConverter} was specified, the {@link Object} is returned as is.
     */
    public Object serialize(T value) {
        if (converter != null)
            return converter.serialize(value);
        return value;
    }

    /**
     * Deserializes a config file value into the corresponding runtime {@link Object}.
     * <p>
     * If an {@link IPaCoConfigConverter} is present, it is called on the value retrieved from the
     * config file, as the type may require a conversion, e.g. (String -> Enum) or (Double -> Float).
     *
     * @param value The serialized {@link Object} retrieved from the config file
     * @return The deserialized runtime object.
     *         If no {@link IPaCoConfigConverter} was specified, the {@link Object} is returned as is.
     * @throws RuntimeException If no {@code converter} was provided and casting the value to the runtime type fails
     */
    @SuppressWarnings("unchecked")
    public T deserialize(Object value) {
        if (this.converter != null)
            return ((IPaCoConfigConverter<T, Object>) this.converter).deserialize(value);
        return (T) value;
    }

    public void setValue(T value) {
        Object serialized = this.serialize(value);
        String key = this.getPath();

        // TODO get the current manager through the config data holder constructor instead of this temporary bandaid solution!
        PaCoConfigManager manager = PaCoConfigManager.getManager(PaCoMainConfig.class);
        manager.getConfig().set(key, serialized);
        // TODO replace this with a bulk save system!
        manager.correctIfNeeded(true);
    }

    /**
     * @return The value of the {@code field}
     * @throws RuntimeException If the field cant be accessed
     */
    @SuppressWarnings("unchecked")
    public T getValue() {
        try {
            return (T) this.field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to read config field: " + this.field.getName(), e);
        }
    }

    public ConfigDataHolderEntry<T> setValidValues(List<String> validValues) {
        this.validValues = validValues;
        return this;
    }

    /** Used to cache the value range (if applicable), which is then used for internal logic */
    @ApiStatus.Internal
    public ConfigDataHolderEntry<T> setRange(@Nullable Number minVal, @Nullable Number maxVal) {
        // We check for null to make sure this won't override "showFullRange", this is technically a bit
        // overkill as both of these methods are flagged as internal, however I say "better safe than sorry!"
        if (this.minVal == null)
            this.minVal = minVal;
        if (this.maxVal == null)
            this.maxVal = maxVal;
        return this;
    }

    /** Used to toggle whether the range should be displayed, regardless of the value. (Useful for small values like byte) */
    @ApiStatus.Internal
    public ConfigDataHolderEntry<T> setShowFullRange(boolean showFullRange, @NotNull Number minVal, @NotNull Number maxVal) {
        this.showFullRange = showFullRange;
        if (showFullRange) {
            this.minVal = minVal;
            this.maxVal = maxVal;
        }
        return this;
    }

    /** @return the config entry comment with "range", "valid values", "padding" or any other optional comment additions */
    @Override
    public String getComment() {
        String comment = "";
        if (!StringUtil.isNullOrEmpty(this.getCommentRaw()))
            comment = super.getComment();
        // Moves the Range comment into a new line if needed
        if (!StringUtil.isNullOrEmpty(comment) && (this.minVal != null || this.maxVal != null || this.validValues != null))
            comment = comment.concat("\n");
        // Adds the Valid Values to the comment
        if (this.validValues != null && !this.validValues.isEmpty())
            comment = comment.concat(" ".repeat(Math.max(0, this.commentPadding))).concat(String.format("Valid Values: %s", String.join(", ", this.validValues)));
        // Adds the Range to the comment
        if (this.showFullRange) {
            comment = comment.concat(" ".repeat(Math.max(0, this.commentPadding))).concat(String.format("Range: %s <= x <= %s", this.minVal, this.maxVal));
        } else {
            if (this.minVal != null && this.maxVal == null) {
                comment = comment.concat(" ".repeat(Math.max(0, this.commentPadding))).concat(String.format("Range: x >= %s", this.minVal));
            } else if (this.minVal == null && this.maxVal != null) {
                comment = comment.concat(" ".repeat(Math.max(0, this.commentPadding))).concat(String.format("Range: x <= %s", this.maxVal));
            } else if (this.minVal != null) {
                comment = comment.concat(" ".repeat(Math.max(0, this.commentPadding))).concat(String.format("Range: %s <= x <= %s", this.minVal, this.maxVal));
            }
        }
        return comment;
    }

    @Override
    public boolean hasComment() {
        return !StringUtil.isNullOrEmpty(this.getComment());
    }

    @Override
    public boolean hasField() {
        return true;
    }
}