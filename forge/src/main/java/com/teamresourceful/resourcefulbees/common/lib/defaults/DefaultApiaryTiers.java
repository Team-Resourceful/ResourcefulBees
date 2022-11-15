package com.teamresourceful.resourcefulbees.common.lib.defaults;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.lib.builders.ApiaryTier;
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
        .output(CommonConfig.T1_APIARY_OUTPUT)
        .amount(CommonConfig.T1_APIARY_QUANTITY::get)
        .blockEntity(ModBlockEntityTypes.T1_APIARY_ENTITY::get)
        .item(ModItems.T1_APIARY_ITEM)
        .build(new ResourceLocation(ResourcefulBees.MOD_ID, "t1"));

    public static final ApiaryTier T2_APIARY = new ApiaryTier.Builder()
            .max(12)
            .time(0.7)
            .output(CommonConfig.T2_APIARY_OUTPUT)
            .amount(CommonConfig.T2_APIARY_QUANTITY::get)
            .blockEntity(ModBlockEntityTypes.T2_APIARY_ENTITY::get)
            .item(ModItems.T2_APIARY_ITEM)
            .build(new ResourceLocation(ResourcefulBees.MOD_ID, "t2"));

    public static final ApiaryTier T3_APIARY = new ApiaryTier.Builder()
            .max(16)
            .time(0.6)
            .output(CommonConfig.T3_APIARY_OUTPUT)
            .amount(CommonConfig.T3_APIARY_QUANTITY::get)
            .blockEntity(ModBlockEntityTypes.T3_APIARY_ENTITY::get)
            .item(ModItems.T3_APIARY_ITEM)
            .build(new ResourceLocation(ResourcefulBees.MOD_ID, "t3"));

    public static final ApiaryTier T4_APIARY = new ApiaryTier.Builder()
            .max(20)
            .time(0.5)
            .output(CommonConfig.T4_APIARY_OUTPUT)
            .amount(CommonConfig.T4_APIARY_QUANTITY::get)
            .blockEntity(ModBlockEntityTypes.T4_APIARY_ENTITY::get)
            .item(ModItems.T4_APIARY_ITEM)
            .build(new ResourceLocation(ResourcefulBees.MOD_ID, "t4"));

    public static void loadDefaults() {
        // NO-OP
    }


}
