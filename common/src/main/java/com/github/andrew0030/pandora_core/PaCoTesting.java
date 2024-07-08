package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.test.ModelLoaderTest;

public class PaCoTesting {
    public static boolean TEST_MODE = true;

    public static void testInitClient() {
        if (TEST_MODE) {
            ModelLoaderTest.init();
        }
    }
}
