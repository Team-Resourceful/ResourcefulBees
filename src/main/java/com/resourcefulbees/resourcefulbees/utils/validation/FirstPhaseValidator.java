package com.resourcefulbees.resourcefulbees.utils.validation;

import com.google.common.base.Splitter;
import com.resourcefulbees.resourcefulbees.api.beedata.BreedData;
import com.resourcefulbees.resourcefulbees.api.beedata.CentrifugeData;
import com.resourcefulbees.resourcefulbees.api.beedata.ColorData;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.utils.color.Color;

import java.util.Iterator;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;
import static com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils.ENTITY_RESOURCE_PATTERN;
import static com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils.TAG_RESOURCE_PATTERN;

public class FirstPhaseValidator {

    private FirstPhaseValidator() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static boolean validate(CustomBeeData bee) {
        validateColorData(bee.getColorData(), bee.getName());
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

    private static void validateColorData(ColorData colorData, String name) {
        if (colorData != null) {
            if (colorData.hasHoneycombColor()) validateColorData(colorData.getHoneycombColor(), name, "Honeycomb");
            if (colorData.hasPrimaryColor()) validateColorData(colorData.getHoneycombColor(), name, "Primary");
            if (colorData.hasSecondaryColor()) validateColorData(colorData.getHoneycombColor(), name, "Secondary");
            if (colorData.hasGlowColor()) validateColorData(colorData.getHoneycombColor(), name, "Glow");
        }
    }

    private static void validateColorData(String color, String name, String colorType) {
        if (!Color.validate(color)) {
            logError(name);
            throw new IllegalArgumentException(String.format("%1$s Color: %2$s is not valid!!", colorType, color));
        }
    }

    private static void validateHoneyBottleColor(HoneyBottleData honeyBottleData, String name) {
        if (honeyBottleData != null && honeyBottleData.hasHoneyColor()) {
            validateColorData(honeyBottleData.getHoneyColor(), name, "Honey Bottle");
        }
    }

    private static void validateCentrifugeData(CentrifugeData centrifugeData, String name) {
        if (centrifugeData != null && centrifugeData.hasCentrifugeOutput()) {
            validateCentrifugeData(centrifugeData.getMainOutput(), name, "Main Output");
            validateCentrifugeData(centrifugeData.getSecondaryOutput(), name, "Secondary Output");
            validateCentrifugeData(centrifugeData.getBottleOutput(), name, "Bottle Output");
            validateCentrifugeData(centrifugeData.getFluidOutput(), name, "Fluid Output");
        }
    }

    private static void validateCentrifugeData(String data, String name, String dataType) {
        if (!data.isEmpty() && ValidatorUtils.isInvalidLocation(data)) {
            logError(name);
            throw new IllegalArgumentException(String.format("Centrifuge %1$s: %2$s has invalid syntax!!", dataType, data));
        }
    }

    private static void validateBreeding(BreedData breedData, String name) {
        if (breedData != null && breedData.isBreedable()) {
            validateParents(breedData.getParent1(), breedData.getParent2(), name, "1", "2");
            validateParents(breedData.getParent2(), breedData.getParent1(), name, "2", "1");

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

    //parameter names are shit
    private static void validateParents(String p1, String p2, String name, String type1, String type2) {
        if (!p1.isEmpty() && p2.isEmpty()) {
            logError(name);
            throw new IllegalArgumentException(String.format("Parent %1$s is empty while Parent %2$s is not!!", type1, type2));
        }
    }

    private static void validateFlower(CustomBeeData beeData, String name) {
        if (beeData.getFlower() == null) {
            logError(name);
            throw new IllegalArgumentException("Flower is missing!");
        }

        if (!(TAG_RESOURCE_PATTERN.matcher(beeData.getFlower()).matches()
                || !ValidatorUtils.isInvalidLocation(beeData.getFlower())
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

