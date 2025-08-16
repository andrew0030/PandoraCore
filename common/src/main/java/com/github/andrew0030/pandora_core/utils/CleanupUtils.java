package com.github.andrew0030.pandora_core.utils;

import java.lang.ref.Cleaner;

public class CleanupUtils {
    private static final Cleaner cleaner = Cleaner.create();

    public static Cleaner.Cleanable registerCleanup(Object object, Runnable doClean) {
        return cleaner.register(object, doClean);
    }
}
