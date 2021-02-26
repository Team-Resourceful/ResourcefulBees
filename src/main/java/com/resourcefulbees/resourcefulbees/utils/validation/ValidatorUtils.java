package com.resourcefulbees.resourcefulbees.utils.validation;

import com.resourcefulbees.resourcefulbees.lib.ModConstants;

import java.util.regex.Pattern;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public class ValidatorUtils {

    private ValidatorUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final Pattern SINGLE_RESOURCE_PATTERN = Pattern.compile("^([\\w-]+):([\\w-]+)$", Pattern.CASE_INSENSITIVE);
    public static final Pattern TAG_RESOURCE_PATTERN = Pattern.compile("^(tag:)([\\w-:]+):([\\w-]+/[\\w-]+|[\\w-]+)$", Pattern.CASE_INSENSITIVE);
    public static final Pattern ENTITY_RESOURCE_PATTERN = Pattern.compile("^(entity:)([\\w-]+):([\\w-]+/[\\w-]+|[\\w-]+)$", Pattern.CASE_INSENSITIVE);

    public static boolean logError(String name, String dataCheckType, String data, String dataType) {
        LOGGER.error("{} Bee {} Check Failed! Please check JSON! \n\tCurrent Value: \"{}\" is not a valid {} - Bee will not be used!",
                name, dataCheckType, data, dataType);
        return false;
    }

    public static boolean logMissingData(String name, String dataType) {
        LOGGER.error("{} bee is missing {} object!",
                name, dataType);
        return false;
    }

    public static boolean logWarn(String name, String dataCheckType, String data, String dataType) {
        LOGGER.warn("{} Bee {} Check Failed! Please check JSON!\n\tCurrent value: \"{}\" is not a valid {} - Bee may not function properly!", name, dataCheckType, data, dataType);
        return true;
    }
}
