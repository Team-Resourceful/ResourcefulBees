package com.teamresourceful.resourcefulbees.common.setup.data.honeydata.fluid;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidAttributesData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyRenderData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.TradeData;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.HashSet;
import java.util.Set;

public record CustomHoneyFluidData(
        String id,
        HoneyRenderData renderData,
        HoneyFluidAttributesData fluidAttributesData,
        LazyHolder<Fluid> stillFluid,
        LazyHolder<Fluid> flowingFluid,
        LazyHolder<Item> fluidBucket,
        LazyHolder<Block> fluidBlock,
        BeekeeperTradeData tradeData,
        Set<Pair<Holder<MobEffect>, Float>> beeconEffects
) implements HoneyFluidData {

    private static final HoneyFluidData DEFAULT = new CustomHoneyFluidData("", CustomHoneyRenderData.DEFAULT, CustomHoneyFluidAttributesData.DEFAULT, LazyHolder.of(BuiltInRegistries.FLUID, Fluids.EMPTY), LazyHolder.of(BuiltInRegistries.FLUID, Fluids.EMPTY), LazyHolder.of(BuiltInRegistries.ITEM, Items.AIR), LazyHolder.of(BuiltInRegistries.BLOCK, Blocks.AIR), TradeData.DEFAULT, new HashSet<>());

    private static final Codec<Pair<Holder<MobEffect>, Float>> BEECON_EFFECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(Pair::getFirst),
            Codec.FLOAT.fieldOf("drainAmount").forGetter(Pair::getSecond)
    ).apply(instance, Pair::of));

    private static Codec<HoneyFluidData> codec(String id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),

                CustomHoneyRenderData.CODEC
                        .optionalFieldOf("rendering", CustomHoneyRenderData.DEFAULT)
                        .forGetter(HoneyFluidData::renderData),

                CustomHoneyFluidAttributesData.CODEC
                        .optionalFieldOf("attributes", CustomHoneyFluidAttributesData.DEFAULT)
                        .forGetter(HoneyFluidData::fluidAttributesData),

                TradeData.CODEC
                        .optionalFieldOf("tradeData", TradeData.DEFAULT)
                        .forGetter(HoneyFluidData::tradeData),

                CodecExtras.set(BEECON_EFFECT_CODEC)
                        .optionalFieldOf("effects", Set.of())
                        .forGetter(HoneyFluidData::beeconEffects)

        ).apply(instance, (honeyId, rendering, attributes, tradeData, effects) ->
                new CustomHoneyFluidData(
                        honeyId,
                        rendering,
                        attributes,
                        LazyHolder.of(
                                BuiltInRegistries.FLUID,
                                ModIdentifier.of(honeyId + "_honey_fluid_source")
                        ),
                        LazyHolder.of(
                                BuiltInRegistries.FLUID,
                                ModIdentifier.of(honeyId + "_honey_fluid_flowing")
                        ),
                        LazyHolder.of(
                                BuiltInRegistries.ITEM,
                                ModIdentifier.of(honeyId + "_honey_bucket")
                        ),
                        LazyHolder.of(
                                BuiltInRegistries.BLOCK,
                                ModIdentifier.of(honeyId + "_honey_fluid_block")
                        ),
                        tradeData,
                        effects
                )
        ));
    }

    public static final HoneyDataSerializer<HoneyFluidData> SERIALIZER = HoneyDataSerializer.of(ModIdentifier.of("fluid"), 1, CustomHoneyFluidData::codec, DEFAULT);

    @Override
    public HoneyDataSerializer<HoneyFluidData> serializer() {
        return SERIALIZER;
    }
}
