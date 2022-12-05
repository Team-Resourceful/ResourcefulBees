package com.teamresourceful.resourcefulbees.common.data.honeydata.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidAttributesData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyRenderData;
import com.teamresourceful.resourcefulbees.common.data.beedata.TradeData;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.recipes.LazyHolders;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public record CustomHoneyFluidData(
        String id,
        HoneyRenderData renderData,
        HoneyFluidAttributesData fluidAttributesData,
        LazyHolder<Fluid> stillFluid,
        LazyHolder<Fluid> flowingFluid,
        LazyHolder<Item> fluidBucket,
        LazyHolder<Block> fluidBlock,
        BeekeeperTradeData tradeData
) implements HoneyFluidData {

    private static final HoneyFluidData DEFAULT = new CustomHoneyFluidData("", CustomHoneyRenderData.DEFAULT, CustomHoneyFluidAttributesData.DEFAULT, LazyHolder.of(Registry.FLUID, Fluids.EMPTY), LazyHolder.of(Registry.FLUID, Fluids.EMPTY), LazyHolder.of(Registry.ITEM, Items.AIR), LazyHolder.of(Registry.BLOCK, Blocks.AIR), TradeData.DEFAULT);
    private static Codec<HoneyFluidData> codec(String id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                CustomHoneyRenderData.CODEC.fieldOf("rendering").orElse(CustomHoneyRenderData.DEFAULT).forGetter(HoneyFluidData::renderData),
                CustomHoneyFluidAttributesData.CODEC.fieldOf("attributes").orElse(CustomHoneyFluidAttributesData.DEFAULT).forGetter(HoneyFluidData::fluidAttributesData),
                LazyHolders.LAZY_FLUID.fieldOf("stillFluid").forGetter(HoneyFluidData::stillFluid),
                LazyHolders.LAZY_FLUID.fieldOf("flowingFluid").forGetter(HoneyFluidData::flowingFluid),
                LazyHolders.LAZY_ITEM.fieldOf("fluidBucket").forGetter(HoneyFluidData::fluidBucket),
                LazyHolders.LAZY_BLOCK.fieldOf("fluidBlock").forGetter(HoneyFluidData::fluidBlock),
                TradeData.CODEC.fieldOf("tradeData").orElse(TradeData.DEFAULT).forGetter(HoneyFluidData::tradeData)
        ).apply(instance, CustomHoneyFluidData::new));
    }
    public static final HoneyDataSerializer<HoneyFluidData> SERIALIZER = HoneyDataSerializer.of(new ModResourceLocation("fluid"), 1, CustomHoneyFluidData::codec, DEFAULT);

    @Override
    public HoneyDataSerializer<HoneyFluidData> serializer() {
        return SERIALIZER;
    }
}
