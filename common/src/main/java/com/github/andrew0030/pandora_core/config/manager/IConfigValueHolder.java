package com.github.andrew0030.pandora_core.config.manager;

public interface IConfigValueHolder<T> {
    void setValue(T value);
    T getValue();
}