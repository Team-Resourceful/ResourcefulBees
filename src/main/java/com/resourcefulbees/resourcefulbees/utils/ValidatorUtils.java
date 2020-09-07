package com.resourcefulbees.resourcefulbees.utils;

import java.util.regex.Pattern;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public class ValidatorUtils {
    public static final Pattern SINGLE_RESOURCE_PATTERN = Pattern.compile("^(\\w+):(\\w+)$", Pattern.CASE_INSENSITIVE);
    public static final Pattern TAG_RESOURCE_PATTERN = Pattern.compile("^(tag:)(\\w+):(\\w+/\\w+|\\w+)$", Pattern.CASE_INSENSITIVE);

    public static boolean logError(String name, String dataCheckType, String data, String dataType){
        LOGGER.error("{} Bee {} Check Failed! Please check JSON! \n\tCurrent Value: \"{}\" is not a valid {} - Bee will not be used!",
                name, dataCheckType, data, dataType);
        return false;
    }


    public static boolean logWarn(String name, String dataCheckType, String data, String dataType){
        LOGGER.warn(name + " Bee " + dataCheckType + " Check Failed! Please check JSON!" +
                "\n\tCurrent value: \"" + data + "\" is not a valid " + dataType + " - Bee may not function properly!");
        return true;
    }

/*    public static boolean validate(CustomBee bee) {
        boolean isValid;

        isValid = validateColor(bee);
        isValid = isValid && validateFlower(bee);
        isValid = isValid && validateMutation(bee);
        isValid = isValid && validateCentrifugeMainOutput(bee);
        isValid = isValid && validateCentrifugeSecondaryOutput(bee);
        isValid = isValid && validateCentrifugeBottleOutput(bee);
        isValid = isValid && validateMaxTimeInHive(bee);
        if (bee.BreedData.isBreedable()) isValid = isValid && validateBreeding(bee);

        return isValid;
    }*//*

    private static boolean validateMutation(CustomBeeData bee) {
*//*        if (!beeHasMutation(bee)) {
            return true;
        }*//*

        if (TAG_RESOURCE_PATTERN.matcher(bee.MutationData.getMutationInput()).matches()) {
            LOGGER.warn("Too early to validate Block Tag for " + bee.getName() + " bee.");
            return true;
        }

        int mutation = -1;

        //validate base block
        Block inputBlock = BeeInfoUtils.getBlock(bee.MutationData.getMutationInput());
        if (BeeInfoUtils.isValidBlock(inputBlock)) {
            Fluid inputFluid = BeeInfoUtils.getFluid(bee.MutationData.getMutationInput());
            Item inputItem = BeeInfoUtils.getItem(bee.MutationData.getMutationInput());
            if (BeeInfoUtils.isValidFluid(inputFluid)) {
                mutation++;
            } else if (BeeInfoUtils.isValidItem(inputItem)) {
                mutation += 2;
            } else return logError(bee.getName(), "Base Block", bee.MutationData.getMutationInput(), "block");
        } else return logError(bee.getName(), "Base Block", bee.MutationData.getMutationInput(), "block");

        //validate mutation block
        Block outputBlock = BeeInfoUtils.getBlock(bee.MutationData.getMutationOutput());
        if (BeeInfoUtils.isValidBlock(outputBlock)) {
            Fluid outputFluid = BeeInfoUtils.getFluid(bee.MutationData.getMutationOutput());
            Item outputItem = BeeInfoUtils.getItem(bee.MutationData.getMutationOutput());
            if (BeeInfoUtils.isValidFluid(outputFluid)) { }
            else if (BeeInfoUtils.isValidItem(outputItem)) {
                mutation += 2;
            } else return logError(bee.getName(), "Mutation Block", bee.MutationData.getMutationOutput(), "block");
        } else return logError(bee.getName(), "Mutation Block", bee.MutationData.getMutationOutput(), "block");

*//*        switch (mutation) {
            case (0) :
                bee.MutationData.setMutationType(MutationTypes.FLUID_TO_FLUID);
                break;
            case (1) :
                bee.setMutationType(MutationTypes.BLOCK_TO_FLUID);
                break;
            case (2) :
                bee.setMutationType(MutationTypes.FLUID_TO_BLOCK);
                break;
            case (3) :
                bee.setMutationType(MutationTypes.BLOCK_TO_BLOCK);
        }*//*
        return true;
    }

*//*    private static boolean beeHasMutation(CustomBee bee) {
        if (TAG_RESOURCE_PATTERN.matcher(bee.getMutationInput()).matches() ||
                SINGLE_RESOURCE_PATTERN.matcher(bee.getMutationInput()).matches() &&
                SINGLE_RESOURCE_PATTERN.matcher(bee.getMutationOutput()).matches()) {
            bee.setMutation(true);
            return true;
        }
        if (Config.SHOW_DEBUG_INFO.get())
            LOGGER.warn(StringUtils.capitalize(bee.getName()) + " - Bee has no mutations or the patterns don't match.");
        return false;
    }*//*


    private static boolean validateCentrifugeMainOutput(CustomBeeData bee) {
        if (!bee.CentrifugeData.getMainOutput().isEmpty()) {
            Item item = BeeInfoUtils.getItem(bee.CentrifugeData.getMainOutput());
            return SINGLE_RESOURCE_PATTERN.matcher(bee.CentrifugeData.getMainOutput()).matches() && BeeInfoUtils.isValidItem(item) ||
                    logError(bee.getName(), "Centrifuge Output", bee.CentrifugeData.getMainOutput(), "item");
        }
        return true;
    }

*//*    private static boolean validateCentrifugeSecondaryOutput(CustomBee bee) {
        if (!bee.getMainOutput().isEmpty()) {
            Item item = BeeInfoUtils.getItem(bee.getSecondaryOutput());
            return SINGLE_RESOURCE_PATTERN.matcher(bee.getSecondaryOutput()).matches() && BeeInfoUtils.isValidItem(item) ||
                    logError(bee.getName(), "Centrifuge Output", bee.getSecondaryOutput(), "item");
        }
        return true;
    }

    private static boolean validateCentrifugeBottleOutput(CustomBee bee) {
        if (!bee.getMainOutput().isEmpty()) {
            Item item = BeeInfoUtils.getItem(bee.getBottleOutput());
            return SINGLE_RESOURCE_PATTERN.matcher(bee.getBottleOutput()).matches() && BeeInfoUtils.isValidItem(item) ||
                    logError(bee.getName(), "Centrifuge Output", bee.getBottleOutput(), "item");
        }
        return true;
    }

    private static boolean validateFlower(CustomBee bee) {
        if(TAG_RESOURCE_PATTERN.matcher(bee.getFlower()).matches())
            return true;
        else {
            Block flower = BeeInfoUtils.getBlock(bee.getFlower());
            return (bee.getFlower().equals(BeeConstants.FLOWER_TAG_ALL) ||
                    bee.getFlower().equals(BeeConstants.FLOWER_TAG_SMALL) ||
                    bee.getFlower().equals(BeeConstants.FLOWER_TAG_TALL) ||
                    BeeInfoUtils.isValidBlock(flower)) ||
                    logError(bee.getName(), "Flower", bee.getFlower(), "flower");
        }
    }

    private static boolean validateFeedItem(CustomBee bee) {
        if(TAG_RESOURCE_PATTERN.matcher(bee.getFeedItem()).matches())
            return true;
        else {
            Item feedItem = BeeInfoUtils.getItem(bee.getFeedItem());
            return (bee.getFlower().equals(BeeConstants.FLOWER_TAG_ALL) ||
                    bee.getFlower().equals(BeeConstants.FLOWER_TAG_SMALL) ||
                    bee.getFlower().equals(BeeConstants.FLOWER_TAG_TALL) ||
                    BeeInfoUtils.isValidItem(feedItem)) ||
                    logError(bee.getName(), "Feed Item", bee.getFlower(), "feed item");
        }
    }

    private static boolean validateColor(CustomBee bee) {
        boolean flag = true;
        if (bee.getHoneycombColor() != null && !bee.getHoneycombColor().isEmpty()) {
            flag = Color.validate(bee.getHoneycombColor());
            if (!flag)
                logError(bee.getName(), "Honeycomb Color", bee.getHoneycombColor() , "color");
        }
        if (flag && bee.getPrimaryColor() != null && !bee.getPrimaryColor().isEmpty()) {
            flag = Color.validate(bee.getPrimaryColor());
            if (!flag)
                logError(bee.getName(), "Primary Color", bee.getPrimaryColor() , "color");
        }
        if (flag && bee.getSecondaryColor() != null && !bee.getSecondaryColor().isEmpty()) {
            flag = Color.validate(bee.getSecondaryColor());
            if (!flag)
                logError(bee.getName(), "Secondary Color", bee.getSecondaryColor() , "color");
        }
        if (flag && bee.getGlowingColor() != null && !bee.getGlowingColor().isEmpty()) {
            flag = Color.validate(bee.getGlowingColor());
            if (!flag)
                logError(bee.getName(), "Glowing Color", bee.getGlowingColor() , "color");
        }
        return flag;
    }*/
}
