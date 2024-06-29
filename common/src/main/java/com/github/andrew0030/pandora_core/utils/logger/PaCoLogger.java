package com.github.andrew0030.pandora_core.utils.logger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaCoLogger {

    /**
     * Creates a {@link Logger} based on the given strings.<br/>
     * If multiple strings are given, they will be stacked with a " | " in between them:<br/>
     * <strong>FirstString | SecondString | ThirdString | FourthString...</strong>
     * @param string The first string, this one is required to create the logger.
     * @param strings The strings that should be stacked after the first one.
     */
    public static Logger create(String string, String... strings) {
        StringBuilder builder = new StringBuilder();
        builder.append(string);
        for (String s : strings) {
            builder.append(" | ");
            builder.append(s);
        }
        return LoggerFactory.getLogger(builder.toString());
    }

    /**
     * Log a message at the <strong>INFO</strong> level if the condition is <strong>true</strong>.
     * @param logger The {@link Logger} that will print the message.
     * @param condition The condition that will be checked.
     * @param message The message string to be logged.
     */
    public static void conditionalInfo(Logger logger, boolean condition, String message) {
        if (condition)
            logger.info(message);
    }

    /**
     * Log a message at the <strong>INFO</strong> level according to the specified format and arguments, if the condition is <strong>true</strong>.
     * @param logger The {@link Logger} that will print the message.
     * @param condition The condition that will be checked.
     * @param message The message string to be logged.
     * @param args A list of arguments.
     */
    public static void conditionalInfo(Logger logger, boolean condition, String message, Object... args) {
        if (condition)
            logger.info(message, args);
    }

    /**
     * Log a message at the <strong>WARN</strong> level if the condition is <strong>true</strong>.
     * @param logger The {@link Logger} that will print the message.
     * @param condition The condition that will be checked.
     * @param message The message string to be logged.
     */
    public static void conditionalWarn(Logger logger, boolean condition, String message) {
        if (condition)
            logger.warn(message);
    }

    /**
     * Log a message at the <strong>WARN</strong> level according to the specified format and arguments, if the condition is <strong>true</strong>.
     * @param logger The {@link Logger} that will print the message.
     * @param condition The condition that will be checked.
     * @param message The message string to be logged.
     * @param args A list of arguments.
     */
    public static void conditionalWarn(Logger logger, boolean condition, String message, Object... args) {
        if (condition)
            logger.warn(message, args);
    }

    /**
     * Log a message at the <strong>ERROR</strong> level if the condition is <strong>true</strong>.
     * @param logger The {@link Logger} that will print the message.
     * @param condition The condition that will be checked.
     * @param message The message string to be logged.
     */
    public static void conditionalError(Logger logger, boolean condition, String message) {
        if (condition)
            logger.error(message);
    }

    /**
     * Log a message at the <strong>ERROR</strong> level according to the specified format and arguments, if the condition is <strong>true</strong>.
     * @param logger The {@link Logger} that will print the message.
     * @param condition The condition that will be checked.
     * @param message The message string to be logged.
     * @param args A list of arguments.
     */
    public static void conditionalError(Logger logger, boolean condition, String message, Object... args) {
        if (condition)
            logger.error(message, args);
    }
}