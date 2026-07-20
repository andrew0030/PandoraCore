package com.github.andrew0030.pandora_core.mixin_interfaces;

public interface IPaCoEditBox {
    boolean pandoraCore$hideBackground();
    boolean pandoraCore$hideRim();
    boolean pandoraCore$forceLineIndicator();
    void pandoraCore$onValueChange(String newText);
    default boolean pandoraCore$isBordered() { return true; }
    default int pandoraCore$getDisplayPos() { return 0; }
    default void pandoraCore$setDisplayPos(int displayPos) {}
    default int pandoraCore$getHighlightPos() { return 0; }
    default void pandoraCore$setHighlightPos(int highlightPos) {}
}