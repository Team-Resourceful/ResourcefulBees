package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public record HoneyBlockData(boolean generate, String name, Color color, float jumpFactor, float speedFactor, Item blockItem, Block block) {

    public static HoneyBlockData getDefault(String name) {
        return new HoneyBlockData(false, name, Color.DEFAULT, 0.5f, 0.4f, Items.AIR, Blocks.AIR);
    }

    public static Codec<HoneyBlockData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> true)).forGetter(honeyBlockData -> true),
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(HoneyBlockData::name),
                Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneyBlockData::color),
                Codec.FLOAT.fieldOf("jumpFactor").orElse(0.5f).forGetter(HoneyBlockData::jumpFactor),
                Codec.FLOAT.fieldOf("speedFactor").orElse(0.4f).forGetter(HoneyBlockData::speedFactor),
                ForgeRegistries.ITEMS.getCodec().fieldOf("honeyBlockItem").orElse(Items.HONEY_BLOCK).forGetter(HoneyBlockData::blockItem),
                ForgeRegistries.BLOCKS.getCodec().fieldOf("honeyBlock").orElse(Blocks.HONEY_BLOCK).forGetter(HoneyBlockData::block)
        ).apply(instance, HoneyBlockData::new));
    }

    public HoneyBlockData {
        if (Boolean.TRUE.equals(CommonConfig.HONEY_GENERATE_BLOCKS.get()) && generate && HoneyRegistry.getRegistry().canGenerate()) {
            RegistryObject<Block> honeyBlock = ModBlocks.HONEY_BLOCKS.register(name + "_honey_block", () -> new CustomHoneyBlock(this));
            ModItems.HONEY_BLOCK_ITEMS.register(name + "_honey_block", () -> new BlockItem(honeyBlock.get(), new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HONEY)));
        }
    }
}
