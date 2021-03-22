package com.resourcefulbees.resourcefulbees.utils.validation;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyEffect;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.Mutation;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.MutationOutput;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils.*;

public class SecondPhaseValidator {

    public static final Logger LOGGER = LogManager.getLogger();

    private SecondPhaseValidator() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void validateHoneyEffects(HoneyBottleData honeyData) {
        List<HoneyEffect> honeyEffects = honeyData.getEffects();
        if (honeyEffects == null || honeyEffects.isEmpty()) return;
        Iterator<HoneyEffect> effectIterator = honeyEffects.iterator();
        while (effectIterator.hasNext()) {
            HoneyEffect honeyEffect = effectIterator.next();
            MobEffect effect = honeyEffect.getEffect();
            if (effect == null) {
                LOGGER.warn("An effect for: {} could not be properly validated and was removed.", honeyData.getHoneyBottleRegistryObject().getId());
                effectIterator.remove();
            } else {
                honeyEffect.setEffect(effect);
            }
        }
        honeyData.setEffects(honeyEffects);
    }

    public static void validateCentrifugeOutputs(CustomBeeData bee) {
        Item mainOutput = getItem(bee.getCentrifugeData().getMainOutput());
        Fluid fluidOutput = getFluid(bee.getCentrifugeData().getMainOutput());
        Item secondaryOutput = getItem(bee.getCentrifugeData().getSecondaryOutput());
        Item bottleOutput = getItem(bee.getCentrifugeData().getBottleOutput());
        bee.getCentrifugeData().setHasCentrifugeOutput((isValidFluid(fluidOutput) || isValidItem(mainOutput)) && isValidItem(secondaryOutput) && isValidItem(bottleOutput));
    }

    public static boolean validateMutation(String name, Mutation mutation) {
        if (mutation.getType() == null) {
            LOGGER.warn("\"type\" could not be validated for {}'s mutation.", name);
            return true;
        }
        if (mutation.getInputID() == null) {
            LOGGER.warn("\"inputID\" does not exist for {}'s mutation.", name);
            return true;
        }
        if (mutation.getOutputs() == null) {
            LOGGER.warn("\"outputs\" does not exist for {}'s mutation.", name);
            return true;
        }

        mutation.getOutputs().removeIf(validateMutationOutputID(name));

        if (mutation.getOutputs().isEmpty()) {
            LOGGER.warn("No valid outputs could be found for {}'s mutation.", name);
            return true;
        }
        return false;
    }

    private static Predicate<MutationOutput> validateMutationOutputID(String name) {
        return mutationOutput -> {
            if (mutationOutput.getOutputID() == null) {
                LOGGER.warn("an instance of \"outputID\" does not exist for {}'s mutation.", name);
                return true;
            }
            return false;
        };
    }
}
