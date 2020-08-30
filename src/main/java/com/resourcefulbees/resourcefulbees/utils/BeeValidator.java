package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.data.BeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public class BeeValidator {
    public static final Pattern SINGLE_RESOURCE_PATTERN = Pattern.compile("^(\\w+):(\\w+)$", Pattern.CASE_INSENSITIVE);
    public static final Pattern TAG_RESOURCE_PATTERN = Pattern.compile("^(tag:)(\\w+):(\\w+/\\w+|\\w+)$", Pattern.CASE_INSENSITIVE);

    private static boolean logError(String name, String dataCheckType, String data, String dataType){
        LOGGER.error(name + " Bee " + dataCheckType + " Check Failed! Please check JSON!" +
                "\n\tCurrent value: \"" + data + "\" is not a valid " + dataType + " - Bee will not be used!");
        return false;
    }

    private static boolean logWarn(String name, String dataCheckType, String data, String dataType){
        LOGGER.warn(name + " Bee " + dataCheckType + " Check Failed! Please check JSON!" +
                "\n\tCurrent value: \"" + data + "\" is not a valid " + dataType + " - Bee may not function properly!");
        return true;
    }

    public static boolean validate(BeeData bee) {
        boolean isValid;

        isValid = validateColor(bee);
        isValid = isValid && validateFlower(bee);
        isValid = isValid && validateMutation(bee);
        isValid = isValid && validateCentrifugeMainOutput(bee);
        isValid = isValid && validateCentrifugeSecondaryOutput(bee);
        isValid = isValid && validateCentrifugeBottleOutput(bee);
        isValid = isValid && validateMaxTimeInHive(bee);
        if (bee.isBreedable()) isValid = isValid && validateBreeding(bee);

        return isValid;
    }

    private static boolean validateMutation(BeeData bee) {
        if (!beeHasMutation(bee)) {
            return true;
        }

        if (TAG_RESOURCE_PATTERN.matcher(bee.getMutationInput()).matches()) {
            LOGGER.warn("Too early to validate Block Tag for " + bee.getName() + " bee.");
            return true;
        }

        int mutation = -1;

        //validate base block
        Block inputBlock = BeeInfoUtils.getBlock(bee.getMutationInput());
        if (BeeInfoUtils.isValidBlock(inputBlock)) {
            Fluid inputFluid = BeeInfoUtils.getFluid(bee.getMutationInput());
            Item inputItem = BeeInfoUtils.getItem(bee.getMutationInput());
            if (BeeInfoUtils.isValidFluid(inputFluid)) {
                mutation++;
            } else if (BeeInfoUtils.isValidItem(inputItem)) {
                mutation += 2;
            } else return logError(bee.getName(), "Base Block", bee.getMutationInput(), "block");
        } else return logError(bee.getName(), "Base Block", bee.getMutationInput(), "block");

        //validate mutation block
        Block outputBlock = BeeInfoUtils.getBlock(bee.getMutationOutput());
        if (BeeInfoUtils.isValidBlock(outputBlock)) {
            Fluid outputFluid = BeeInfoUtils.getFluid(bee.getMutationOutput());
            Item outputItem = BeeInfoUtils.getItem(bee.getMutationOutput());
            if (BeeInfoUtils.isValidFluid(outputFluid)) { }
            else if (BeeInfoUtils.isValidItem(outputItem)) {
                mutation += 2;
            } else return logError(bee.getName(), "Mutation Block", bee.getMutationOutput(), "block");
        } else return logError(bee.getName(), "Mutation Block", bee.getMutationOutput(), "block");

        switch (mutation) {
            case (0) :
                bee.setMutationType(MutationTypes.FLUID_TO_FLUID);
                break;
            case (1) :
                bee.setMutationType(MutationTypes.BLOCK_TO_FLUID);
                break;
            case (2) :
                bee.setMutationType(MutationTypes.FLUID_TO_BLOCK);
                break;
            case (3) :
                bee.setMutationType(MutationTypes.BLOCK_TO_BLOCK);
        }
        return true;
    }

    private static boolean beeHasMutation(BeeData bee) {
        if (TAG_RESOURCE_PATTERN.matcher(bee.getMutationInput()).matches() ||
                SINGLE_RESOURCE_PATTERN.matcher(bee.getMutationInput()).matches() &&
                SINGLE_RESOURCE_PATTERN.matcher(bee.getMutationOutput()).matches()) {
            bee.setMutation(true);
            return true;
        }
        if (Config.SHOW_DEBUG_INFO.get())
            LOGGER.warn(StringUtils.capitalize(bee.getName()) + " - Bee has no mutations or the patterns don't match.");
        return false;
    }

    private static boolean validateBreeding(BeeData bee) {
        return !bee.getParent1().equals(bee.getParent2()) ||
                logWarn(bee.getName(), "breeding", (bee.getParent1() + " and " + bee.getParent2()),
                "are the same parents. Child bee will not spawn from breeding.");
    }

    private static boolean validateMaxTimeInHive(BeeData bee) {
        double time = bee.getMaxTimeInHive();
        return time >= BeeConstants.MIN_HIVE_TIME && time == Math.floor(time) && !Double.isInfinite(time) ||
                logWarn(bee.getName(), "Time In Hive", String.valueOf(bee.getMaxTimeInHive()),
                "time. Value must be greater than or equal to " + BeeConstants.MIN_HIVE_TIME);
    }

    private static boolean validateCentrifugeMainOutput(BeeData bee) {
        if (!bee.getMainOutput().isEmpty()) {
            Item item = BeeInfoUtils.getItem(bee.getMainOutput());
            return SINGLE_RESOURCE_PATTERN.matcher(bee.getMainOutput()).matches() && BeeInfoUtils.isValidItem(item) ||
                    logError(bee.getName(), "Centrifuge Output", bee.getMainOutput(), "item");
        }
        return true;
    }

    private static boolean validateCentrifugeSecondaryOutput(BeeData bee) {
        if (!bee.getMainOutput().isEmpty()) {
            Item item = BeeInfoUtils.getItem(bee.getSecondaryOutput());
            return SINGLE_RESOURCE_PATTERN.matcher(bee.getSecondaryOutput()).matches() && BeeInfoUtils.isValidItem(item) ||
                    logError(bee.getName(), "Centrifuge Output", bee.getSecondaryOutput(), "item");
        }
        return true;
    }

    private static boolean validateCentrifugeBottleOutput(BeeData bee) {
        if (!bee.getMainOutput().isEmpty()) {
            Item item = BeeInfoUtils.getItem(bee.getBottleOutput());
            return SINGLE_RESOURCE_PATTERN.matcher(bee.getBottleOutput()).matches() && BeeInfoUtils.isValidItem(item) ||
                    logError(bee.getName(), "Centrifuge Output", bee.getBottleOutput(), "item");
        }
        return true;
    }

    private static boolean validateFlower(BeeData bee) {
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

    private static boolean validateFeedItem(BeeData bee) {
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

    private static boolean validateColor(BeeData bee) {
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
    }
}
