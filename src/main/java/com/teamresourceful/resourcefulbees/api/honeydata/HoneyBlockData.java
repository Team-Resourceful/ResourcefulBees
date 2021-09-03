package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

public class HoneyBlockData {

    public static HoneyBlockData getDefault(String name) {
        return new HoneyBlockData(false, name, Color.DEFAULT, 0.5f, 0.4f);
    }

    public static Codec<HoneyBlockData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> true)).forGetter(honeyBlockData -> true),
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(HoneyBlockData::getName),
                Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneyBlockData::getColor),
                Codec.FLOAT.fieldOf("jumpFactor").orElse(0.5f).forGetter(HoneyBlockData::getJumpFactor),
                Codec.FLOAT.fieldOf("speedFactor").orElse(0.4f).forGetter(HoneyBlockData::getSpeedFactor)
        ).apply(instance, HoneyBlockData::new));
    }

    private final String name;
    private final Color color;
    private final float jumpFactor;
    private final float speedFactor;
    private RegistryObject<Item> blockItem;
    private RegistryObject<Block> block;

    public HoneyBlockData(boolean generate, String name, Color color, float jumpFactor, float speedFactor){
        this.name = name;
        this.color = color;
        this.jumpFactor = jumpFactor;
        this.speedFactor = speedFactor;

        if (CommonConfig.HONEY_GENERATE_BLOCKS.get() && generate) {
            block = ModBlocks.HONEY_BLOCKS.register(name + "_honey_block", () -> new CustomHoneyBlock(this));
            blockItem = ModItems.HONEY_BLOCK_ITEMS.register(name + "_honey_block", () -> new BlockItem(block.get(), new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
        }
    }

    public String getName() { return name; }

    public Color getColor() {
        return color;
    }

    public float getJumpFactor() {
        return jumpFactor;
    }

    public float getSpeedFactor() {
        return speedFactor;
    }

    public RegistryObject<Item> getBlockItem() {
        return blockItem;
    }

    public RegistryObject<Block> getBlock() {
        return block;
    }
}
