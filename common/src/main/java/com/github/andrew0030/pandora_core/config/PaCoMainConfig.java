package com.github.andrew0030.pandora_core.config;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.annotations.ConfigType;
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

@PaCoConfig.Config(type = ConfigType.COMMON, modId = PandoraCore.MOD_ID, name = "main")
@PaCoConfig.SubFolder("paco_sub_folder")
public class PaCoMainConfig {

    @PaCoConfigValues.Comment("This is a test comment")
    @PaCoConfigValues.BooleanValue
    public static Boolean falseValue = false;

    @PaCoConfigValues.Comment("This is an additional test comment")
    @PaCoConfigValues.BooleanValue
    public static boolean trueValue = true;

    @PaCoConfigValues.Comment("This is an Integer Value")
    @PaCoConfigValues.IntegerValue(showFullRange = true)
    public static Integer integerValue1 = 11;

    @PaCoConfigValues.Comment("A Byte value is right here")
    @PaCoConfigValues.ByteValue(showFullRange = true)
    public static byte byteValue = -50;

    @PaCoConfigValues.Comment("A Max Val Byte instead of byte")
    @PaCoConfigValues.ByteValue
    public static Byte maxByteVal = Byte.MAX_VALUE;

    @PaCoConfigValues.IntegerValue(minValue = 0, maxValue = 100)
    public static int rangedAltInt = 100;

    @PaCoConfigValues.Comment("\nThis is a comment over a ranged int")
    @PaCoConfigValues.IntegerValue(minValue = 0, maxValue = 10)
    public static int commentedRangeInt = 5;

    @PaCoConfigValues.Comment("This value should only have a \"visible\" min value")
    @PaCoConfigValues.IntegerValue(minValue = 0)
    public static int minOnlyValue = 200;

    @PaCoConfigValues.Comment("This value should only have a \"visible\" max value")
    @PaCoConfigValues.IntegerValue(maxValue = 500)
    public static int maxOnlyValue = 300;

    @PaCoConfigValues.Comment("""
            There should be a category under this entry.
            This is the last boolean value before declared classes.
            """)
    @PaCoConfigValues.BooleanValue
    public static boolean lastBoolean = false;

    @PaCoConfig.Comment("   This is a test category for booleans.\n   It holds 2 boolean values!   ")
    @PaCoConfig.Category("booleans")
    public static class BooleanSubCategory {

        @PaCoConfigValues.Comment("\nFirst Boolean inside the boolean category")
        @PaCoConfigValues.BooleanValue
        public static Boolean falseValue = false;

        @PaCoConfigValues.Comment("\nSecond Boolean inside the boolean category")
        @PaCoConfigValues.BooleanValue
        public static Boolean trueValue = true;
    }

    @PaCoConfig.Comment("This category contains 2 other categories.")
    @PaCoConfig.Category("nested_outer")
    public static class NestedOuter {

        @PaCoConfig.Category("nested_inner1")
        public static class NestedInner1 {

            @PaCoConfigValues.BooleanValue
            public static Boolean someValue = true;
        }

        @PaCoConfig.Category("nested_inner2")
        public static class NestedInner2 {

            @PaCoConfigValues.BooleanValue
            public static Boolean someValue = true;
        }

        @PaCoConfig.Category("nested_inner3")
        public static class NestedInner3 {

            @PaCoConfigValues.BooleanValue
            public static Boolean someValue = true;
        }

        @PaCoConfig.Category("nested_inner4")
        public static class NestedInner4 {

            @PaCoConfigValues.BooleanValue
            public static Boolean someValue = true;
        }

        @PaCoConfig.Category("nested_inner5")
        public static class NestedInner5 {

            @PaCoConfigValues.BooleanValue
            public static Boolean someValue = true;
        }
    }

    @PaCoConfig.Comment("A Category containing Integers")
    @PaCoConfig.Category("integers")
    public static class IntegerSubCategory {

        @PaCoConfigValues.Comment("This is an Integer Value")
        @PaCoConfigValues.IntegerValue
        public static Integer integerValue2 = 12;

        @PaCoConfigValues.Comment("This is the last integer in the category")
        @PaCoConfigValues.IntegerValue
        public static int integerValue3 = 13;
    }

    @PaCoConfig.Comment("A Category containing Shorts")
    @PaCoConfig.Category("shorts")
    public static class ShortSubCategory {

        @PaCoConfigValues.Comment("A Short value\nWith a multiline comment above!")
        @PaCoConfigValues.ShortValue(maxValue = 0)
        public static short shortValue = -50;

        @PaCoConfigValues.Comment("A Full Range Short")
        @PaCoConfigValues.ShortValue(showFullRange = true)
        public static Short fullRangeShort = 32000;
    }

    @PaCoConfig.Comment("A Category containing Doubles")
    @PaCoConfig.Category("doubles")
    public static class DoubleSubCategory {

        @PaCoConfigValues.Comment("This is a Double version of PI")
        @PaCoConfigValues.DoubleValue
        public static double doublePI = Math.PI;

        @PaCoConfigValues.Comment("This is a normal Double value")
        @PaCoConfigValues.DoubleValue
        public static Double doubleValue = 22.354;
    }

    @PaCoConfig.Comment("This Category contains Floats and Longs.")
    @PaCoConfig.Category("floatsAndLongs")
    public static class FloatsAndLongs {

        @PaCoConfigValues.Comment("This is a Float version of PI")
        @PaCoConfigValues.FloatValue
        public static float float_PI = Mth.PI;

        @PaCoConfigValues.Comment("Comment above a Long")
        @PaCoConfigValues.LongValue
        public static long someLongValue = 500L;

        @PaCoConfigValues.Comment("Comment above a second Long")
        @PaCoConfigValues.LongValue
        public static Long someOtherLongValue = 500000L;
    }

    @PaCoConfig.Comment("This Category contains Strings")
    @PaCoConfig.Category("strings")
    public static class Strings {

        @PaCoConfigValues.Comment("Comment above a String")
        @PaCoConfigValues.StringValue
        public static String someStringValue = "This is a String";

        @PaCoConfigValues.Comment("And another String!")
        @PaCoConfigValues.StringValue
        public static String yetAnotherString = "This is an additional String";
    }

    @PaCoConfig.Comment("This Category contains Lists")
    @PaCoConfig.Category("lists")
    public static class Lists {

        @PaCoConfigValues.Comment("Comment above a String List")
        @PaCoConfigValues.ListValue(elementType = String.class)
        public static List<String> stringList = Arrays.asList("Element 1", "Element 2", "Element 3");

        @PaCoConfigValues.Comment("Comment above an Integer List")
        @PaCoConfigValues.ListValue(elementType = Integer.class)
        public static List<Integer> integerList = Arrays.asList(111, 22, 3333);

        @PaCoConfigValues.Comment("Comment above a Boolean List")
        @PaCoConfigValues.ListValue(elementType = Boolean.class)
        public static List<Boolean> booleanList = Arrays.asList(false, true, true, false, true);
    }

    @PaCoConfig.Comment("This Category contains Enums")
    @PaCoConfig.Category("enums")
    public static class Enums {
        @PaCoConfigValues.Comment("Comment above an Enum")
        @PaCoConfigValues.EnumValue
        public static Difficulty enumValue = Difficulty.MEDIUM;
        public enum Difficulty {
            EASY,
            MEDIUM,
            HARD
        }
    }
}