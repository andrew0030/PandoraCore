package com.github.andrew0030.pandora_core.config.manager;

import net.minecraft.util.StringUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigDataHolder {

    private final Field field;
    private Function<Object, Object> converter;
    private List<String> validValues;
    private String comment;
    private int commentPadding = 1;
    private Object minVal;
    private Object maxVal;

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

    public Object convert(Object value) {
        if (converter != null) {
            return converter.apply(value);
        }
        return value;
    }

    public ConfigDataHolder setValidValues(List<String> validValues) {
        this.validValues = validValues;
        return this;
    }

    public void setComment(String comment) {
        this.setComment(comment, 1);
    }

    public void setComment(String comment, int padding) {
        // Splits the comment into lines, applies padding to each line, and joins them back together
        this.comment = Arrays.stream(comment.split("\n"))
                .map(line -> " ".repeat(Math.max(0, padding)) + line)
                .collect(Collectors.joining("\n"));
        this.commentPadding = padding;
    }

    public String getComment() {
        String comment = this.comment != null ? this.comment : "";
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

    public boolean hasComment() {
        return !StringUtil.isNullOrEmpty(this.getComment());
    }

    public ConfigDataHolder setRange(Object minVal, Object maxVal) {
        this.minVal = minVal;
        this.maxVal = maxVal;
        return this;
    }
}