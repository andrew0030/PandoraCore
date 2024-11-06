package com.github.andrew0030.pandora_core.config;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfigValues;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.List;

//TODO: create a config system using NightConfig
// - Probably add annotations as its a nice clean and simple way to create configs
// - Create a Screen builder that uses the PaCoConfigs and well creates a screen
// - Try to wrap the ForgeConfigSpec so I can generate PaCoConfigs for all the mods that use ForgeConfigSpec but don't provide any screen
// - Use optional mixins to create config screen providers, for the popular config APIs. Probably this will work by grabbing the config and using the package to get the mod they belong to.
// - Alternatively if mods like Catalogue or Mod Menu are installed, check their config screen entry points
// - Maybe keep track of mods that used the PaCoMainConfig system...

@PaCoConfig(modId = PandoraCore.MOD_ID, name = "main")
public class PaCoMainConfig {

    @PaCoConfigValues.Comment("This is a test comment")
    @PaCoConfigValues.BooleanValue
    public boolean falseValue = false;

    @PaCoConfigValues.Comment("This is an additional test comment")
    @PaCoConfigValues.BooleanValue
    public boolean trueValue = true;

    @PaCoConfigValues.Comment("This is an Integer Value, with index: '1'")
    @PaCoConfigValues.IntegerValue
    public int integerValue1 = 11;

    @PaCoConfigValues.Comment("This is an Integer Value, with index: '2'")
    @PaCoConfigValues.IntegerValue
    public int integerValue2 = 12;

    @PaCoConfigValues.Comment("This is an Integer Value, with index: '3'")
    @PaCoConfigValues.IntegerValue
    public int integerValue3 = 13;

    @PaCoConfigValues.IntegerValue(minValue = 0, maxValue = 100)
    public int rangedAltInt = 100;

    @PaCoConfigValues.Comment("\nThis is a comment over a ranged int")
    @PaCoConfigValues.IntegerValue(minValue = 0, maxValue = 10)
    public int commentedRangeInt = 5;

    @PaCoConfigValues.Comment("This value should only have a \"visible\" min value")
    @PaCoConfigValues.IntegerValue(minValue = 0)
    public int minOnlyValue = 200;

    @PaCoConfigValues.Comment("This value should only have a \"visible\" max value")
    @PaCoConfigValues.IntegerValue(maxValue = 500)
    public int maxOnlyValue = 300;

    @PaCoConfigValues.Comment("Comment above a String")
    @PaCoConfigValues.StringValue
    public String someStringValue = "This is a String";

    @PaCoConfigValues.Comment("This is a Double version of PI")
    @PaCoConfigValues.DoubleValue
    public double doublePI = Math.PI;

    @PaCoConfigValues.Comment("This is a Float version of PI")
    @PaCoConfigValues.FloatValue
    public float float_PI = Mth.PI;

    @PaCoConfigValues.Comment("Comment above a Long")
    @PaCoConfigValues.LongValue
    public long someLongValue = 500L;

    @PaCoConfigValues.Comment("Comment above a String List")
    @PaCoConfigValues.ListValue(elementType = String.class)
    public List<String> stringList = Arrays.asList("Element 1", "Element 2", "Element 3");

    @PaCoConfigValues.Comment("Comment above an Integer List")
    @PaCoConfigValues.ListValue(elementType = Integer.class)
    public List<Integer> integerList = Arrays.asList(111, 22, 3333);

    @PaCoConfigValues.Comment("Comment above a Boolean List")
    @PaCoConfigValues.ListValue(elementType = Boolean.class)
    public List<Boolean> booleanList = Arrays.asList(false, true, true, false, true);

    @PaCoConfigValues.Comment("Comment above an Enum")
    @PaCoConfigValues.EnumValue
    public Difficulty enumValue = Difficulty.MEDIUM;
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}