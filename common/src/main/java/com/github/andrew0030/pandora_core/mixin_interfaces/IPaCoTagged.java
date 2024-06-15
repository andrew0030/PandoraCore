package com.github.andrew0030.pandora_core.mixin_interfaces;

import java.util.Collection;

public interface IPaCoTagged {
    void pandoraCore$addTag(String name);
    void pandoraCore$lockTags();
    Collection<String> pandoraCore$getTags();
    boolean pandoraCore$hasTag(String tag);
}