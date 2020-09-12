package com.resourcefulbees.resourcefulbees.utils.validation;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

import static com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils.*;
import static com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils.TAG_RESOURCE_PATTERN;

public class SecondPhaseValidator {

    public static void validateMutation(CustomBeeData bee) {
        if (bee.getMutationData().hasMutation()) {
            if (TAG_RESOURCE_PATTERN.matcher(bee.getMutationData().getMutationInput()).matches()) {
                validateMutationTag(bee);
            } else {
                Item inputItem;
                Item outputItem;
                Fluid inputFluid;
                Fluid outputFluid;

                switch (bee.getMutationData().getMutationType()) {
                    case BLOCK_TO_BLOCK:
                        inputItem = getItem(bee.getMutationData().getMutationInput());
                        outputItem = getItem(bee.getMutationData().getMutationOutput());
                        bee.getMutationData().setHasMutation(isValidItem(inputItem) && isValidItem(outputItem));
                        break;
                    case BLOCK_TO_FLUID:
                        inputItem = getItem(bee.getMutationData().getMutationInput());
                        outputFluid = getFluid(bee.getMutationData().getMutationOutput());
                        bee.getMutationData().setHasMutation(isValidItem(inputItem) && isValidFluid(outputFluid));
                        break;
                    case FLUID_TO_BLOCK:
                        inputFluid = getFluid(bee.getMutationData().getMutationInput());
                        outputItem = getItem(bee.getMutationData().getMutationOutput());
                        bee.getMutationData().setHasMutation(isValidFluid(inputFluid) && isValidItem(outputItem));
                    case FLUID_TO_FLUID:
                        inputFluid = getFluid(bee.getMutationData().getMutationInput());
                        outputFluid = getFluid(bee.getMutationData().getMutationOutput());
                        bee.getMutationData().setHasMutation(isValidFluid(inputFluid) && isValidFluid(outputFluid));
                        break;
                    default:
                        bee.getMutationData().setHasMutation(false);
                }
            }
        }
    }

    private static void validateMutationTag(CustomBeeData bee) {
        String tag = bee.getMutationData().getMutationInput().replace(BeeConstants.TAG_PREFIX, "");
        Item outputItem;
        Fluid outputFluid;

        switch (bee.getMutationData().getMutationType()) {
            case BLOCK_TO_BLOCK:
                outputItem = getItem(bee.getMutationData().getMutationOutput());
                bee.getMutationData().setHasMutation(getBlockTag(tag) != null && isValidItem(outputItem));
                break;
            case BLOCK_TO_FLUID:
                outputFluid = getFluid(bee.getMutationData().getMutationOutput());
                bee.getMutationData().setHasMutation(getBlockTag(tag) != null && isValidFluid(outputFluid));
                break;
            case FLUID_TO_BLOCK:
                outputItem = getItem(bee.getMutationData().getMutationOutput());
                bee.getMutationData().setHasMutation(getFluidTag(tag) != null && isValidItem(outputItem));
                break;
            case FLUID_TO_FLUID:
                outputFluid = getFluid(bee.getMutationData().getMutationOutput());
                bee.getMutationData().setHasMutation(getFluidTag(tag) != null && isValidFluid(outputFluid));
                break;
            default:
                bee.getMutationData().setHasMutation(false);
        }
    }

    public static void validateCentrifugeOutputs(CustomBeeData bee) {
        Item mainOutput = getItem(bee.getCentrifugeData().getMainOutput());
        Item secondaryOutput = getItem(bee.getCentrifugeData().getSecondaryOutput());
        Item bottleOutput = getItem(bee.getCentrifugeData().getBottleOutput());
        bee.getCentrifugeData().setHasCentrifugeOutput(isValidItem(mainOutput) && isValidItem(secondaryOutput) && isValidItem(bottleOutput));
    }
}

