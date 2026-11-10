package com.github.andrew0030.pandora_core.utils;

import java.lang.ref.Cleaner;

/** Utility wrapper around {@link Cleaner} for registering {@code GC} hooks. */
public class CleanupUtils {
    private static final Cleaner cleaner = Cleaner.create();

    /**
     * Registers an action to execute when the target {@code object} is garbage collected.
     *
     * @param object  The {@link Object} to monitor for {@code GC}
     * @param doClean the cleanup action to run
     * @return The cleanable instance
     * @apiNote
     * <b>IMPORTANT:</b> To prevent memory leaks, capturing the monitored {@code object} in the cleanup task must be strictly avoided!
     * <ul>
     *     <li>Do <b>NOT</b> use a lambda for {@code doClean}.</li>
     *     <li>Do <b>NOT</b> use a non-static inner class for {@code doClean}.</li>
     *     <li>Do <b>NOT</b> store the {@code object} inside the {@code doClean} runnable.</li>
     * </ul>
     */
    public static Cleaner.Cleanable registerCleanup(Object object, Runnable doClean) {
        return cleaner.register(object, doClean);
    }
}