package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.data.honey.*;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.FluidAttributeData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.FluidRenderData;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class DummyHoneyData {

    public static final HoneyData DUMMY_HONEY_DATA = new HoneyData(
            "template",
            new HoneyBottleData(
                    "template",
                    ConstantColors.blue,
                    2,
                    4,
                    Rarity.EPIC,
                    List.of(
                            new HoneyEffect(MobEffects.WITHER, 25, 2, 0.75f),
                            new HoneyEffect(MobEffects.INVISIBILITY, 50, 1, 1f)
                    ),
                    LazyHolder.of(Registry.ITEM, Items.HONEY_BOTTLE)
            ),
            new HoneyBlockData(
                    true,
                    "template",
                    ConstantColors.blue,
                    2,
                    8,
                    LazyHolder.of(Registry.ITEM, Items.HONEY_BLOCK),
                    LazyHolder.of(Registry.BLOCK, Blocks.HONEY_BLOCK)
            ),
            new HoneyFluidData(
                    true,
                    "template",
                    FluidRenderData.DEFAULT,
                    FluidAttributeData.DEFAULT,
                    LazyHolder.of(Registry.FLUID, ModFluids.HONEY_STILL.get()),
                    LazyHolder.of(Registry.FLUID, ModFluids.HONEY_FLOWING.get()),
                    LazyHolder.of(Registry.ITEM, ModItems.HONEY_FLUID_BUCKET.get()),
                    LazyHolder.of(Registry.BLOCK, ModBlocks.HONEY_FLUID_BLOCK.get())
            )
    );
}
