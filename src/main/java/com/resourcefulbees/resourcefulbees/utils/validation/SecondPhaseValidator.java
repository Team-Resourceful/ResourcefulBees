package com.resourcefulbees.resourcefulbees.utils.validation;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.effects.ModEffects;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;

import static com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils.*;

public class SecondPhaseValidator {

    public static final Logger LOGGER = LogManager.getLogger();

    public static void validateHoneyEffects(HoneyBottleData honeyData) {
        List<HoneyBottleData.HoneyEffect> honeyEffects = honeyData.getEffects();
        if (honeyEffects == null || honeyEffects.isEmpty()) return;
        Iterator<HoneyBottleData.HoneyEffect> effectIterator = honeyEffects.iterator();
        while (effectIterator.hasNext()) {
            HoneyBottleData.HoneyEffect honeyEffect = effectIterator.next();
            Effect effect = honeyEffect.getEffect();
            if (effect == null) {
                LOGGER.warn(String.format("An effect for: $s could not be properly validated and was removed.", honeyData.getHoneyBottleRegistryObject().getId().toString()));
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
}

