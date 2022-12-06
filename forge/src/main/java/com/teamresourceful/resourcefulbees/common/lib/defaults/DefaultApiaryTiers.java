package com.teamresourceful.resourcefulbees.common.lib.defaults;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.ApiaryConfig;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.resources.ResourceLocation;

public final class DefaultApiaryTiers {

    private DefaultApiaryTiers() {
        throw new UtilityClassError();
    }

    public static final ApiaryTier T1_APIARY = new ApiaryTier.Builder()
        .max(8)
        .time(0.8)
        .output(() -> ApiaryConfig.tierOneApiaryOutput)
        .amount(() -> ApiaryConfig.tierOneApiaryQuantity)
        .blockEntity(ModBlockEntityTypes.T1_APIARY_ENTITY::get)
        .item(ModItems.T1_APIARY_ITEM)
        .build(new ResourceLocation(ResourcefulBees.MOD_ID, "t1"));

    public static final ApiaryTier T2_APIARY = new ApiaryTier.Builder()
            .max(12)
            .time(0.7)
            .output(() -> ApiaryConfig.tierTwoApiaryOutput)
            .amount(() -> ApiaryConfig.tierTwoApiaryQuantity)
            .blockEntity(ModBlockEntityTypes.T2_APIARY_ENTITY::get)
            .item(ModItems.T2_APIARY_ITEM)
            .build(new ResourceLocation(ResourcefulBees.MOD_ID, "t2"));

    public static final ApiaryTier T3_APIARY = new ApiaryTier.Builder()
            .max(16)
            .time(0.6)
            .output(() -> ApiaryConfig.tierThreeApiaryOutput)
            .amount(() -> ApiaryConfig.tierThreeApiaryQuantity)
            .blockEntity(ModBlockEntityTypes.T3_APIARY_ENTITY::get)
            .item(ModItems.T3_APIARY_ITEM)
            .build(new ResourceLocation(ResourcefulBees.MOD_ID, "t3"));

    public static final ApiaryTier T4_APIARY = new ApiaryTier.Builder()
            .max(20)
            .time(0.5)
            .output(() -> ApiaryConfig.tierFourApiaryOutput)
            .amount(() -> ApiaryConfig.tierFourApiaryQuantity)
            .blockEntity(ModBlockEntityTypes.T4_APIARY_ENTITY::get)
            .item(ModItems.T4_APIARY_ITEM)
            .build(new ResourceLocation(ResourcefulBees.MOD_ID, "t4"));

    public static void loadDefaults() {
        // NO-OP
    }


}
