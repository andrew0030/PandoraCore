package com.github.andrew0030.pandora_core.config.manager;

import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigDataHolder {

    private final Field field;
    private Function<Object, Object> converter;
    private List<String> validValues;
    private Number minVal;
    private Number maxVal;
    private String comment;
    private int commentPadding = 1;

    public ConfigDataHolder(Field field) {
        this.field = field;
    }

    public Field getField() {
        return this.field;
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

    public ConfigDataHolder setComment(String comment) {
        this.setComment(comment, 1);
        return this;
    }

    public ConfigDataHolder setComment(String comment, int padding) {
        // Splits the comment into lines, applies padding to each line, and joins them back together
        this.comment = Arrays.stream(comment.split("\n"))
                .map(line -> " ".repeat(Math.max(0, padding)) + line)
                .collect(Collectors.joining("\n"));
        this.commentPadding = padding;
        return this;
    }

    /** @return the config entry comment as is, meaning this doesn't contain "range" or "valid values" or any other optional comment additions */
    public String getCommentRaw() {
        return this.comment != null ? this.comment : "";
    }

    /** @return the config entry comment with "range" or "valid values" or any other optional comment additions */
    public String getComment() {
        String comment = this.getCommentRaw();
        // Moves the Range comment into a new line if needed
        if (!StringUtil.isNullOrEmpty(comment) && (this.minVal != null || this.maxVal != null || this.validValues != null))
            comment = comment.concat("\n");
        // Adds the Valid Values to the comment
        if (this.validValues != null && !this.validValues.isEmpty())
            comment = comment.concat(" ".repeat(Math.max(0, this.commentPadding))).concat(String.format("Valid Values: %s", String.join(", ", this.validValues)));
        // Adds the Range to the comment
        if (this.minVal != null && this.maxVal == null) {
            comment = comment.concat(" ".repeat(Math.max(0, this.commentPadding))).concat(String.format("Range: x >= %s", this.minVal));
        } else if (this.minVal == null && this.maxVal != null) {
            comment = comment.concat(" ".repeat(Math.max(0, this.commentPadding))).concat(String.format("Range: x <= %s", this.maxVal));
        } else if (this.minVal != null) {
            comment = comment.concat(" ".repeat(Math.max(0, this.commentPadding))).concat(String.format("Range: %s <= x <= %s", this.minVal, this.maxVal));
        }
        return comment;
    }

    /** @return whether the config entry has a comment */
    public boolean hasComment() {
        return !StringUtil.isNullOrEmpty(this.getComment());
    }

    /** Used to cache the value range (if applicable), which is then used for internal logic */
    @ApiStatus.Internal
    public ConfigDataHolder setRange(Number minVal, Number maxVal) {
        this.minVal = minVal;
        this.maxVal = maxVal;
        return this;
    }
}