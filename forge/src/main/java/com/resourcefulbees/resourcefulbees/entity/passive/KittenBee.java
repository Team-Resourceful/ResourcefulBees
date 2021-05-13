package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyEffect;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.world.effect.MobEffects;

public class KittenBee {

    private KittenBee() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static HoneyBottleData honeyBottleData = null;

    public static HoneyBottleData getHoneyBottleData() {
        if (honeyBottleData == null) {
            HoneyBottleData.Builder builder = new HoneyBottleData.Builder("catnip", 8, 0.9f, "#BD5331");
            HoneyEffect speed = new HoneyEffect(MobEffects.MOVEMENT_SPEED.getRegistryName().toString(), 2400, 2, 1);
            HoneyEffect nightVision = new HoneyEffect(MobEffects.NIGHT_VISION.getRegistryName().toString(), 2400, 0, 1);
            HoneyEffect jump = new HoneyEffect(MobEffects.JUMP.getRegistryName().toString(), 2400, 1, 1);
            builder.addEffect(speed).addEffect(nightVision).addEffect(jump);
            honeyBottleData = builder.build();
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
