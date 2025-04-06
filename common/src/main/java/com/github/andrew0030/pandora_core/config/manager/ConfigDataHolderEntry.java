package com.github.andrew0030.pandora_core.config.manager;

import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

public class ConfigDataHolderEntry extends ConfigDataHolder {

    private final Field field;
    private Function<Object, Object> converter;
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

    public ConfigDataHolder setConverter(Function<Object, Object> converter) {
        this.converter = converter;
        return this;
    }

    /**
     * A converter {@link Function} may be present depending on the type of the field, the {@link Function}
     * is called on the value retrieved from the config file, before its stored in the field, as the type
     * may require a conversion, e.g. (String -> Enum) or (Double -> Float).
     * @param value the {@link Object} that will be converted to a different {@link Object}
     * @return the converted {@link Object}, if no converter {@link Function} was specified, the {@link Object} is returned as is
     */
    public Object convert(Object value) {
        if (converter != null)
            return converter.apply(value);
        return value;
    }

    public ConfigDataHolder setValidValues(List<String> validValues) {
        this.validValues = validValues;
        return this;
    }

    /** Used to cache the value range (if applicable), which is then used for internal logic */
    @ApiStatus.Internal
    public ConfigDataHolder setRange(@Nullable Number minVal, @Nullable Number maxVal) {
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
    public ConfigDataHolder setShowFullRange(boolean showFullRange, @NotNull Number minVal, @NotNull Number maxVal) {
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