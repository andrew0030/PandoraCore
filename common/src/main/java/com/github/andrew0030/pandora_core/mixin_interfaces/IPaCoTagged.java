package com.github.andrew0030.pandora_core.mixin_interfaces;

import java.util.Collection;

public interface IPaCoTagged {
    void addPaCoTag(String name);
    void lockPaCoTags();
    Collection<String> getPaCoTags();
    boolean hasPaCoTag(String tag);
}
