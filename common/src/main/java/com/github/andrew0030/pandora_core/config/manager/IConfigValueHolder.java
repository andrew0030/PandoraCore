package com.github.andrew0030.pandora_core.config.manager;

import java.util.List;

public interface IConfigValueHolder<T> {
    void setValue(T value);
    T getValue();
    List<String> getValidValues();
    T getMinVal();
    T getMaxVal();
}