package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.common.data.beedata.TradeData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.CustomHoneyBlockData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.bottle.CustomHoneyBottleData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.bottle.CustomHoneyBottleEffectData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.bottle.CustomHoneyFoodData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.fluid.CustomHoneyFluidAttributesData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.fluid.CustomHoneyFluidData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.fluid.CustomHoneyRenderData;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;

public class DummyHoneyData {

    public static final Map<ResourceLocation, com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData<?>> DATA = Map.of(
            CustomHoneyBottleData.SERIALIZER.id(), new CustomHoneyBottleData(
                    "template",
                    ConstantColors.blue,
                    new CustomHoneyFoodData(
                            2,
                            4,
                            false,
                            false,
                            List.of(
                                    new CustomHoneyBottleEffectData(LazyHolder.of(Registry.MOB_EFFECT, MobEffects.WITHER), 25, 2, 0.75f),
                                    new CustomHoneyBottleEffectData(LazyHolder.of(Registry.MOB_EFFECT, MobEffects.INVISIBILITY), 50, 1, 1f)
                            )
                    ),
                    Rarity.EPIC,
                    LazyHolder.of(Registry.ITEM, Items.HONEY_BOTTLE),
                    TradeData.DEFAULT
            ),
            CustomHoneyBlockData.SERIALIZER.id(), new CustomHoneyBlockData(
                    ConstantColors.blue,
                    2,
                    8,
                    LazyHolder.of(Registry.ITEM, Items.HONEY_BLOCK),
                    LazyHolder.of(Registry.BLOCK, Blocks.HONEY_BLOCK),
                    TradeData.DEFAULT
            ),
            CustomHoneyFluidData.SERIALIZER.id(), new CustomHoneyFluidData(
                    "template",
                    CustomHoneyRenderData.DEFAULT,
                    CustomHoneyFluidAttributesData.DEFAULT,
                    LazyHolder.of(Registry.FLUID, ModFluids.HONEY_STILL.get()),
                    LazyHolder.of(Registry.FLUID, ModFluids.HONEY_FLOWING.get()),
                    LazyHolder.of(Registry.ITEM, ModItems.HONEY_FLUID_BUCKET.get()),
                    LazyHolder.of(Registry.BLOCK, ModBlocks.HONEY_FLUID_BLOCK.get()),
                    TradeData.DEFAULT
            )
    );
}
