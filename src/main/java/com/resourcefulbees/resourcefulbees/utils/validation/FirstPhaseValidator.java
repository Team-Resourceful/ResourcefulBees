package com.resourcefulbees.resourcefulbees.utils.validation;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.utils.color.Color;

import static com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils.*;

public class FirstPhaseValidator {

    public static boolean validate(CustomBeeData bee) {
        return validateHoneycombColor(bee);
    }

    private static boolean validateHoneycombColor(CustomBeeData beeData) {
        if(beeData.getColorData() != null) {
            if (beeData.getColorData().hasHoneycombColor()) {
                return validatePrimaryColor(validateColor(beeData.getName(), "HoneycombColor", beeData.getColorData().getHoneycombColor()), beeData);
            } else if (!beeData.getColorData().hasHoneycombColor()) {
                return validatePrimaryColor(true, beeData);
            }
        }
        return logMissingData(beeData.getName(), "ColorData");
    }

    private static boolean validatePrimaryColor(boolean isValid, CustomBeeData beeData) {
        if(beeData.getColorData() != null) {
            if (isValid && beeData.getColorData().hasPrimaryColor()) {
                return validateSecondaryColor(validateColor(beeData.getName(), "PrimaryColor", beeData.getColorData().getPrimaryColor()), beeData);
            } else if (isValid && !beeData.getColorData().hasPrimaryColor()) {
                return validateSecondaryColor(true, beeData);
            }
        }
        return logMissingData(beeData.getName(), "ColorData");
    }

    private static boolean validateSecondaryColor(boolean isValid, CustomBeeData beeData) {
        if (beeData.getColorData() != null) {
            if (isValid && beeData.getColorData().hasSecondaryColor()) {
                return validateGlowColor(validateColor(beeData.getName(), "SecondaryColor", beeData.getColorData().getSecondaryColor()), beeData);
            } else if (isValid && !beeData.getColorData().hasSecondaryColor()) {
                return validateGlowColor(true, beeData);
            }
        }
        return logMissingData(beeData.getName(), "ColorData");
    }

    private static boolean validateGlowColor(boolean isValid, CustomBeeData beeData) {
        if (beeData.getColorData() != null) {
            if (isValid && beeData.getColorData().hasGlowColor()) {
                return validateMutation(validateColor(beeData.getName(), "GlowColor", beeData.getColorData().getGlowColor()), beeData);
            } else if (isValid && !beeData.getColorData().hasGlowColor()) {
                return validateMutation(true, beeData);
            }
        }
        return logMissingData(beeData.getName(), "ColorData");
    }

    private static boolean validateColor(String name, String dataCheckType, String color) {
        if (Color.validate(color)) {
            return true;
        } else {
            return logError(name, dataCheckType, color, "color");
        }
    }

    private static boolean validateMutation(boolean isValid, CustomBeeData beeData) {
        if (beeData.getMutationData() != null) {
            if (isValid && beeData.getMutationData().hasMutation() && beeData.getMutationData().getMutationType() != MutationTypes.NONE) {
                return validateCentrifugeData(validateMutation(beeData), beeData);
            } else if (isValid && !beeData.getMutationData().hasMutation()) {
                return validateCentrifugeData(true, beeData);
            }
        }
        return logMissingData(beeData.getName(), "MutationData");
    }

    private static boolean validateMutation(CustomBeeData beeData) {
        return validateMutationInput(beeData.getName(), beeData.getMutationData().getMutationInput())
                && validateMutationOutput(beeData.getName(), beeData.getMutationData().getMutationOutput());
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
        if (beeData.getCentrifugeData() != null) {
            if (isValid && beeData.getCentrifugeData().hasCentrifugeOutput()) {
                return validateBreeding(validateCentrifugeData(beeData), beeData);
            } else if (isValid && !beeData.getCentrifugeData().hasCentrifugeOutput()) {
                return validateBreeding(true, beeData);
            }
        }
        return logMissingData(beeData.getName(), "CentrifugeData");
    }

    private static boolean validateCentrifugeData(CustomBeeData beeData) {
        return validateCentrifugeMainOutput(beeData)
                && validateCentrifugeSecondaryOutput(beeData)
                && validateCentrifugeBottleOutput(beeData);
    }

    private static boolean validateCentrifugeMainOutput(CustomBeeData beeData) {
        if (!beeData.getCentrifugeData().getMainOutput().isEmpty() && SINGLE_RESOURCE_PATTERN.matcher(beeData.getCentrifugeData().getMainOutput()).matches()) {
            return true;
        } else {
            return logError(beeData.getName(), "Centrifuge Output", beeData.getCentrifugeData().getMainOutput(), "item");
        }
    }

    private static boolean validateCentrifugeSecondaryOutput(CustomBeeData beeData) {
        if (!beeData.getCentrifugeData().getSecondaryOutput().isEmpty() && SINGLE_RESOURCE_PATTERN.matcher(beeData.getCentrifugeData().getSecondaryOutput()).matches()) {
            return true;
        } else {
            return logError(beeData.getName(), "Centrifuge Output", beeData.getCentrifugeData().getSecondaryOutput(), "item");
        }
    }

    private static boolean validateCentrifugeBottleOutput(CustomBeeData beeData) {
        if (!beeData.getCentrifugeData().getMainOutput().isEmpty() && SINGLE_RESOURCE_PATTERN.matcher(beeData.getCentrifugeData().getBottleOutput()).matches()) {
            return true;
        } else {
            return logError(beeData.getName(), "Centrifuge Output", beeData.getCentrifugeData().getBottleOutput(), "item");
        }
    }

    private static boolean validateBreeding(boolean isValid, CustomBeeData beeData) {
        if (beeData.getBreedData() != null) {
            if (isValid && beeData.getBreedData().isBreedable()) {
                return validateFlower (!beeData.getBreedData().getParent1().equals(beeData.getBreedData().getParent2())
                        || (beeData.getBreedData().getParent1().isEmpty() && beeData.getBreedData().getParent2().isEmpty())
                        || logWarn(beeData.getName(),
                        "breeding",
                        (beeData.getBreedData().getParent1() + " and " + beeData.getBreedData().getParent2()),
                                "are the same parents. Child bee will not spawn from breeding."),
                        beeData);
            }
            if (isValid && !beeData.getBreedData().isBreedable()) {
                return true;
            }
        }
        return logMissingData(beeData.getName(), "BreedData");
    }

    private static boolean validateFlower(boolean isValid, CustomBeeData beeData) {
        if (isValid && beeData.getFlower() != null) {
            return TAG_RESOURCE_PATTERN.matcher(beeData.getFlower()).matches()
                    || beeData.getFlower().equals(BeeConstants.FLOWER_TAG_ALL)
                    || beeData.getFlower().equals(BeeConstants.FLOWER_TAG_SMALL)
                    || beeData.getFlower().equals(BeeConstants.FLOWER_TAG_TALL);
        }

        return logMissingData(beeData.getName(), "flower");
    }
}

