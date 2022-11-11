package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.codecs.recipes.LazyHolders;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public record HoneyBlockData(boolean generate, String name, Color color, float jumpFactor, float speedFactor, LazyHolder<Item> blockItem, LazyHolder<Block> block) {

    public static HoneyBlockData getDefault(String name) {
        return new HoneyBlockData(false, name, Color.DEFAULT, 0.5f, 0.4f, new LazyHolder<>(Registry.ITEM, new ResourceLocation("air")), new LazyHolder<>(Registry.BLOCK, new ResourceLocation("air")));
    }

    public static Codec<HoneyBlockData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> true)).forGetter(honeyBlockData -> true),
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(HoneyBlockData::name),
                Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneyBlockData::color),
                Codec.FLOAT.fieldOf("jumpFactor").orElse(0.5f).forGetter(HoneyBlockData::jumpFactor),
                Codec.FLOAT.fieldOf("speedFactor").orElse(0.4f).forGetter(HoneyBlockData::speedFactor),
                LazyHolders.LAZY_ITEM.fieldOf("honeyBlockItem").orElse(CodecUtils.itemHolder("honey_block")).forGetter(HoneyBlockData::blockItem),
                LazyHolders.LAZY_BLOCK.fieldOf("honeyBlock").orElse(CodecUtils.blockHolder("honey_block")).forGetter(HoneyBlockData::block)
        ).apply(instance, HoneyBlockData::new));
    }

    public HoneyBlockData {
        if (Boolean.TRUE.equals(CommonConfig.HONEY_GENERATE_BLOCKS.get()) && generate && HoneyRegistry.getRegistry().canGenerate()) {
            RegistryEntry<Block> honeyBlock = ModBlocks.HONEY_BLOCKS.register(name + "_honey_block", () -> new CustomHoneyBlock(this));
            ModItems.HONEY_BLOCK_ITEMS.register(name + "_honey_block", () -> new BlockItem(honeyBlock.get(), new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HONEY)));
        }
    }
}
