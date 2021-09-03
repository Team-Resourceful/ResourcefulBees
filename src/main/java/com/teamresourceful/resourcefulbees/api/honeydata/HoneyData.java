package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyFluidBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.fluids.HoneyFluidAttributes;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBucketItem;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class HoneyData {

    public static Codec<HoneyData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                HoneyBottleData.codec(name).fieldOf("bottleData").orElseGet((Consumer<String>) s -> ResourcefulBees.LOGGER.error("bottleData is REQUIRED!"), null).forGetter(HoneyData::getBottleData),
                HoneyBlockData.codec(name).fieldOf("blockData").orElse(HoneyBlockData.getDefault(name)).forGetter(HoneyData::getBlockData),
                HoneyFluidData.codec(name).fieldOf("fluidData").orElse(HoneyFluidData.getDefault(name)).forGetter(HoneyData::getFluidData)
        ).apply(instance, (bottle, block, fluid) -> new HoneyData(name, bottle, block, fluid)));
    }

    private final String name;
    private final HoneyBottleData bottleData;
    private final HoneyBlockData blockData;
    private final HoneyFluidData fluidData;

    public HoneyData(String name, HoneyBottleData bottleData, HoneyBlockData blockData, HoneyFluidData fluidData) {
        this.name = name;
        this.bottleData = bottleData;
        this.blockData = blockData;
        this.fluidData = fluidData;
    }

    public String getName() {
        return name;
    }

    public HoneyBottleData getBottleData() {
        return bottleData;
    }

    public HoneyBlockData getBlockData() {
        return blockData;
    }

    public HoneyFluidData getFluidData() {
        return fluidData;
    }
}
