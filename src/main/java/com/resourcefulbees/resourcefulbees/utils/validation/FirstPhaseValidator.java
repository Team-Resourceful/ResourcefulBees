package com.resourcefulbees.resourcefulbees.utils.validation;

import com.google.common.base.Splitter;
import com.resourcefulbees.resourcefulbees.api.beedata.*;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.utils.color.Color;

import java.util.Iterator;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;
import static com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils.*;

public class FirstPhaseValidator {

    private FirstPhaseValidator() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static boolean validate(CustomBeeData bee) {
        validateHoneycombColor(bee.getColorData(), bee.getName());
        validatePrimaryColor(bee.getColorData(), bee.getName());
        validateSecondaryColor(bee.getColorData(), bee.getName());
        validateGlowColor(bee.getColorData(), bee.getName());
        validateCentrifugeData(bee.getCentrifugeData(), bee.getName());
        validateBreeding(bee.getBreedData(), bee.getName());
        validateFlower(bee, bee.getName());
        if (bee.getApiaryOutputAmounts() != null) validateCustomApiaryOutputAmounts(bee, bee.getName());
        return true;
    }

    public static boolean validate(HoneyBottleData honeyData) {
        validateHoneyBottleColor(honeyData, honeyData.getName());
        validateEffectValues(honeyData, honeyData.getName());
        return true;
    }


    private static void validateHoneycombColor(ColorData colorData, String name) {
        if (colorData != null && colorData.hasHoneycombColor() && !Color.validate(colorData.getHoneycombColor())) {
            logError(name);
            throw new IllegalArgumentException(String.format("Honeycomb Color: %1$s is not valid!!", colorData.getHoneycombColor()));
        }
    }

    private static void validatePrimaryColor(ColorData colorData, String name) {
        if (colorData != null && colorData.hasPrimaryColor() && !Color.validate(colorData.getPrimaryColor())) {
            logError(name);
            throw new IllegalArgumentException(String.format("Primary Color: %1$s is not valid!!", colorData.getPrimaryColor()));
        }
    }

    private static void validateSecondaryColor(ColorData colorData, String name) {
        if (colorData != null && colorData.hasSecondaryColor() && !Color.validate(colorData.getSecondaryColor())) {
            logError(name);
            throw new IllegalArgumentException(String.format("Secondary Color: %1$s is not valid!!", colorData.getSecondaryColor()));
        }
    }

    private static void validateGlowColor(ColorData colorData, String name) {
        if (colorData != null && colorData.hasGlowColor() && !Color.validate(colorData.getGlowColor())) {
            logError(name);
            throw new IllegalArgumentException(String.format("Glow Color: %1$s is not valid!!", colorData.getGlowColor()));
        }
    }

    private static void validateHoneyBottleColor(HoneyBottleData honeyBottleData, String name) {
        if (honeyBottleData != null && honeyBottleData.hasHoneyColor() && !Color.validate(honeyBottleData.getHoneyColor())) {
            logError(name);
            throw new IllegalArgumentException(String.format("Honey Bottle Color: %1$s is not valid!!", honeyBottleData.getHoneyColor()));
        }
    }

    private static void validateCentrifugeData(CentrifugeData centrifugeData, String name) {
        if (centrifugeData != null && centrifugeData.hasCentrifugeOutput()) {
            if (centrifugeData.getMainOutput().isEmpty() || !SINGLE_RESOURCE_PATTERN.matcher(centrifugeData.getMainOutput()).matches()) {
                logError(name);
                throw new IllegalArgumentException(String.format("Centrifuge Main Output: %1$s has invalid syntax!!", centrifugeData.getMainOutput()));
            }
            if (!centrifugeData.getSecondaryOutput().isEmpty() && !SINGLE_RESOURCE_PATTERN.matcher(centrifugeData.getMainOutput()).matches()) {
                logError(name);
                throw new IllegalArgumentException(String.format("Centrifuge Secondary Output: %1$s has invalid syntax!!", centrifugeData.getSecondaryOutput()));
            }
            if (!centrifugeData.getBottleOutput().isEmpty() && !SINGLE_RESOURCE_PATTERN.matcher(centrifugeData.getMainOutput()).matches()) {
                logError(name);
                throw new IllegalArgumentException(String.format("Centrifuge Bottle Output: %1$s has invalid syntax!!", centrifugeData.getMainOutput()));
            }
        }
    }

    private static void validateBreeding(BreedData breedData, String name) {
        if (breedData != null && breedData.isBreedable()) {
            if (breedData.getParent1().isEmpty() && !breedData.getParent2().isEmpty()) {
                logError(name);
                throw new IllegalArgumentException("Parent 1 is empty while Parent 2 is not!!");
            }
            if (!breedData.getParent1().isEmpty() && breedData.getParent2().isEmpty()) {
                logError(name);
                throw new IllegalArgumentException("Parent 2 is empty while Parent 1 is not!!");
            }

            if (breedData.hasParents()) {
                Iterator<String> parent1 = Splitter.on(",").trimResults().split(breedData.getParent1()).iterator();
                Iterator<String> parent2 = Splitter.on(",").trimResults().split(breedData.getParent2()).iterator();

                while (parent1.hasNext() && parent2.hasNext()) {
                    String p1 = parent1.next();
                    String p2 = parent2.next();
                    if (p1.equals(p2)) {
                        logError(name);
                        throw new IllegalArgumentException(String.format("Parent 1: %1$s and Parent 2: %2$s are the same parents!!", p1, p2));
                    }
                }
            }
        }
    }

    private static void validateFlower(CustomBeeData beeData, String name) {
        if (beeData.getFlower() == null) {
            logError(name);
            throw new IllegalArgumentException("Flower is missing!");
        }

        if (!(TAG_RESOURCE_PATTERN.matcher(beeData.getFlower()).matches()
                || SINGLE_RESOURCE_PATTERN.matcher(beeData.getFlower()).matches()
                || ENTITY_RESOURCE_PATTERN.matcher(beeData.getFlower()).matches()
                || beeData.getFlower().equals(BeeConstants.FLOWER_TAG_ALL)
                || beeData.getFlower().equals(BeeConstants.FLOWER_TAG_SMALL)
                || beeData.getFlower().equals(BeeConstants.FLOWER_TAG_TALL))) {
            logError(name);
            throw new IllegalArgumentException(String.format("Flower: %1$s has invalid syntax!", beeData.getFlower()));
        }
    }

    private static void validateCustomApiaryOutputAmounts(CustomBeeData beeData, String name) {
        for (int i = 0; i < 4; i++) {
            if (beeData.getApiaryOutputAmounts()[i] == 0 || beeData.getApiaryOutputAmounts()[i] < -1) {
                logError(name);
                throw new IllegalArgumentException(String.format("Custom Apiary output amount at index %2$s is invalid! Value: %1$s", beeData.getApiaryOutputAmounts()[i], i));
            }
        }
    }

    private static void validateEffectValues(HoneyBottleData honeyData, String name) {
        if (honeyData.getEffects() != null && !honeyData.getEffects().isEmpty()) {
            honeyData.getEffects().forEach(honeyEffect -> {
                if (!honeyEffect.isEffectIDValid()) {
                    logError(name);
                    throw new IllegalArgumentException(String.format("Custom effect is not valid! Value: %s", honeyEffect.getEffectID()));
                }
            });
        }
    }

    private static void logError(String name) {
        LOGGER.error("{} bee has failed validation!", name);
    }


}

