package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;

import static com.resourcefulbees.resourcefulbees.utils.ValidatorUtils.*;

public class FirstPhaseValidator {

    public static boolean validate(CustomBeeData bee) {
        return validateHoneycombColor(bee);
    }

    private static boolean validateHoneycombColor(CustomBeeData beeData) {
        if (beeData.ColorData.hasHoneycombColor()) {
            return validatePrimaryColor(validateColor(beeData.getName(), "HoneycombColor", beeData.ColorData.getHoneycombColor()), beeData);
        } else if (!beeData.ColorData.hasHoneycombColor()) {
            return validatePrimaryColor(true, beeData);
        } else {
            return false;
        }
    }

    private static boolean validatePrimaryColor(boolean isValid, CustomBeeData beeData) {
        if (isValid && beeData.ColorData.hasPrimaryColor()) {
            return validateSecondaryColor(validateColor(beeData.getName(), "PrimaryColor", beeData.ColorData.getPrimaryColor()), beeData);
        } else if (isValid && !beeData.ColorData.hasPrimaryColor()) {
            return validateSecondaryColor(true, beeData);
        } else {
            return false;
        }
    }

    private static boolean validateSecondaryColor(boolean isValid, CustomBeeData beeData) {
        if (isValid && beeData.ColorData.hasPrimaryColor()) {
            return validateGlowColor(validateColor(beeData.getName(), "SecondaryColor", beeData.ColorData.getSecondaryColor()), beeData);
        } else if (isValid && !beeData.ColorData.hasSecondaryColor()) {
            return validateGlowColor(true, beeData);
        } else {
            return false;
        }
    }

    private static boolean validateGlowColor(boolean isValid, CustomBeeData beeData) {
        if (isValid && beeData.ColorData.hasGlowColor()) {
            return validateMutation(validateColor(beeData.getName(), "GlowColor", beeData.ColorData.getGlowColor()), beeData);
        } else if (isValid && !beeData.ColorData.hasGlowColor()) {
            return validateMutation(true, beeData);
        } else {
            return false;
        }
    }

    private static boolean validateColor(String name, String dataCheckType, String color) {
        if (Color.validate(color)) {
            return true;
        } else {
            return logError(name, dataCheckType, color, "color");
        }
    }

    private static boolean validateMutation(boolean isValid, CustomBeeData beeData) {
        if (isValid && beeData.MutationData.hasMutation() && beeData.MutationData.getMutationType() != MutationTypes.NONE) {
            return validateCentrifugeData(validateMutation(beeData), beeData);
        } else if (isValid && !beeData.MutationData.hasMutation()) {
            return validateCentrifugeData(true, beeData);
        } else {
            return false;
        }
    }

    private static boolean validateMutation(CustomBeeData beeData) {
        return validateMutationInput(beeData.getName(), beeData.MutationData.getMutationInput())
                && validateMutationOutput(beeData.getName(), beeData.MutationData.getMutationOutput());
    }

    private static boolean validateMutationInput(String name, String input) {
        if (TAG_RESOURCE_PATTERN.matcher(input).matches() || SINGLE_RESOURCE_PATTERN.matcher(input).matches()) {
            return true;
        } else {
            return logError(name, "Mutation Input", input, "mutation");
        }
    }

    private static boolean validateMutationOutput(String name, String output) {
        if (SINGLE_RESOURCE_PATTERN.matcher(output).matches()) {
            return true;
        } else {
            return logError(name, "Mutation Output", output, "mutation");
        }
    }

    private static boolean validateCentrifugeData(boolean isValid, CustomBeeData beeData) {
        if (isValid && beeData.CentrifugeData.hasCentrifugeOutput()) {
            return validateBreeding(validateCentrifugeData(beeData), beeData);
        } else if (isValid && !beeData.CentrifugeData.hasCentrifugeOutput()) {
            return validateBreeding(true, beeData);
        } else {
            return false;
        }
    }

    private static boolean validateCentrifugeData(CustomBeeData beeData) {
        return validateCentrifugeMainOutput(beeData)
                && validateCentrifugeSecondaryOutput(beeData)
                && validateCentrifugeBottleOutput(beeData);
    }

    private static boolean validateCentrifugeMainOutput(CustomBeeData beeData) {
        if (!beeData.CentrifugeData.getMainOutput().isEmpty() && SINGLE_RESOURCE_PATTERN.matcher(beeData.CentrifugeData.getMainOutput()).matches()) {
            return true;
        } else {
            return logError(beeData.getName(), "Centrifuge Output", beeData.CentrifugeData.getMainOutput(), "item");
        }
    }

    private static boolean validateCentrifugeSecondaryOutput(CustomBeeData beeData) {
        if (!beeData.CentrifugeData.getSecondaryOutput().isEmpty() && SINGLE_RESOURCE_PATTERN.matcher(beeData.CentrifugeData.getSecondaryOutput()).matches()) {
            return true;
        } else {
            return logError(beeData.getName(), "Centrifuge Output", beeData.CentrifugeData.getSecondaryOutput(), "item");
        }
    }

    private static boolean validateCentrifugeBottleOutput(CustomBeeData beeData) {
        if (!beeData.CentrifugeData.getMainOutput().isEmpty() && SINGLE_RESOURCE_PATTERN.matcher(beeData.CentrifugeData.getBottleOutput()).matches()) {
            return true;
        } else {
            return logError(beeData.getName(), "Centrifuge Output", beeData.CentrifugeData.getBottleOutput(), "item");
        }
    }

    private static boolean validateBreeding(boolean isValid, CustomBeeData beeData) {
        if (isValid && beeData.BreedData.isBreedable()) {
            return !beeData.BreedData.getParent1().equals(beeData.BreedData.getParent2()) || (beeData.BreedData.getParent1().isEmpty() && beeData.BreedData.getParent2().isEmpty()) ||
                    logWarn(beeData.getName(), "breeding", (beeData.BreedData.getParent1() + " and " + beeData.BreedData.getParent2()),
                            "are the same parents. Child bee will not spawn from breeding.");
        } else {
            return true;
        }
    }
}

