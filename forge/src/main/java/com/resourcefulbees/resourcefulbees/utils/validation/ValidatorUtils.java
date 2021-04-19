package com.resourcefulbees.resourcefulbees.utils.validation;

import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.resources.ResourceLocation;

import java.util.regex.Pattern;

public class ValidatorUtils {

    private ValidatorUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final Pattern TAG_RESOURCE_PATTERN = Pattern.compile("^(tag:)([\\w-]+):([\\w-]+/[\\w-]+|[\\w-]+)$", Pattern.CASE_INSENSITIVE);
    public static final Pattern ENTITY_RESOURCE_PATTERN = Pattern.compile("^(entity:)([\\w-]+):([\\w-]+/[\\w-]+|[\\w-]+)$", Pattern.CASE_INSENSITIVE);

    public static boolean isInvalidLocation(String location) {
        return ResourceLocation.tryParse(location) == null;
    }
}
