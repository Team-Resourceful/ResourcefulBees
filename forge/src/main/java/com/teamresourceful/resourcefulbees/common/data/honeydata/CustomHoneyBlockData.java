package com.teamresourceful.resourcefulbees.common.data.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyBlockData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyDataSerializer;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.TradeData;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.recipes.LazyHolders;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record CustomHoneyBlockData(
        Color color,
        float jumpFactor,
        float speedFactor,
        LazyHolder<Item> blockItem,
        LazyHolder<Block> block,
        BeekeeperTradeData tradeData
) implements HoneyBlockData {

    private static final CustomHoneyBlockData DEFAULT = new CustomHoneyBlockData(Color.DEFAULT, 0.5f, 0.4f, LazyHolder.of(Registry.ITEM, Items.AIR), LazyHolder.of(Registry.BLOCK, Blocks.AIR), TradeData.DEFAULT);
    private static final Codec<HoneyBlockData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneyBlockData::color),
            Codec.FLOAT.fieldOf("jumpFactor").orElse(0.5f).forGetter(HoneyBlockData::jumpFactor),
            Codec.FLOAT.fieldOf("speedFactor").orElse(0.4f).forGetter(HoneyBlockData::speedFactor),
            LazyHolders.LAZY_ITEM.fieldOf("honeyBlockItem").orElse(LazyHolder.of(Registry.ITEM, Items.HONEY_BLOCK)).forGetter(HoneyBlockData::blockItem),
            LazyHolders.LAZY_BLOCK.fieldOf("honeyBlock").orElse(LazyHolder.of(Registry.BLOCK, Blocks.HONEY_BLOCK)).forGetter(HoneyBlockData::block),
            TradeData.CODEC.fieldOf("tradeData").orElse(TradeData.DEFAULT).forGetter(HoneyBlockData::tradeData)
    ).apply(instance, CustomHoneyBlockData::new));
    public static final HoneyDataSerializer<HoneyBlockData> SERIALIZER = HoneyDataSerializer.of(new ModResourceLocation("block"), 1, id -> CODEC, DEFAULT);

    @Override
    public HoneyDataSerializer<HoneyBlockData> serializer() {
        return SERIALIZER;
    }
}
