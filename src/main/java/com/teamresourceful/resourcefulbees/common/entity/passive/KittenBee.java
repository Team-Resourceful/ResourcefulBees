package com.teamresourceful.resourcefulbees.common.entity.passive;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBottleData;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyEffect;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.potion.Effects;

import java.util.LinkedList;
import java.util.List;

public class KittenBee {

    private KittenBee() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static HoneyBottleData honeyBottleData = null;

    public static HoneyBottleData getHoneyBottleData() {
        if (honeyBottleData == null) {
            List<HoneyEffect> effects = new LinkedList<>();
            effects.add(new HoneyEffect(Effects.MOVEMENT_SPEED, 2400, 2, 1));
            effects.add(new HoneyEffect(Effects.JUMP, 2400, 1, 1));
            effects.add(new HoneyEffect(Effects.NIGHT_VISION, 2400, 0, 1));
            honeyBottleData = new HoneyBottleData(8, 0.9f, Color.parse("#BD5331"), false, false, false, effects)
                    .setName("catnip");
            honeyBottleData.setHoneyBlockRegistryObject(ModBlocks.CATNIP_HONEY_BLOCK);
            honeyBottleData.setHoneyStillFluidRegistryObject(ModFluids.CATNIP_HONEY_STILL);
            honeyBottleData.setHoneyFlowingFluidRegistryObject(ModFluids.CATNIP_HONEY_FLOWING);
            honeyBottleData.setHoneyBlockItemRegistryObject(ModItems.CATNIP_HONEY_BLOCK_ITEM);
            honeyBottleData.setHoneyBucketItemRegistryObject(ModItems.CATNIP_HONEY_FLUID_BUCKET);
            honeyBottleData.setHoneyBottleRegistryObject(ModItems.CATNIP_HONEY_BOTTLE);
        }
        return honeyBottleData;
    }
}
