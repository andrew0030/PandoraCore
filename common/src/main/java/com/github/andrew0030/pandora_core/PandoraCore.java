package com.github.andrew0030.pandora_core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PandoraCore {
    public static final String MOD_ID = "pandora_core";
    public static final String MOD_NAME = "Pandora Core";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    private static final List<String> MODS = new ArrayList<>();

    /** Common Init */
    public static void init() {
        addPaCoManagedMod(MOD_ID);
        addPaCoManagedMod("swampier_swamps");
        addPaCoManagedMod("table_top_craft");
        addPaCoManagedMod("online_detector");
        addPaCoManagedMod("test_mod_1");
        addPaCoManagedMod("tacos_mc");
        addPaCoManagedMod("some_mod");
        addPaCoManagedMod("this_does_not_exist");
        addPaCoManagedMod("place_holder");
        addPaCoManagedMod("kinda_far_down");
    }

    /** Thread Safe Common Init */
    public static void initThreadSafe() {

    }

    public static List<String> getPaCoManagedMods() {
        return MODS;
    }

    public static void addPaCoManagedMod(String modId) {
        if (MODS.contains(modId))
            throw new RuntimeException("Attempted to add already existing PaCo managed Mod: " + modId);
        MODS.add(modId);
    }
}