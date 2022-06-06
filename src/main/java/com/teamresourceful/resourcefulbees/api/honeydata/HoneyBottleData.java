package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;

public record HoneyBottleData(String name, Color color, int hunger, float saturation, List<HoneyEffect> effects, Item honeyBottle) {

    public static Codec<HoneyBottleData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(HoneyBottleData::name),
                Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneyBottleData::color),
                Codec.INT.fieldOf("hunger").orElse(1).forGetter(HoneyBottleData::hunger),
                Codec.FLOAT.fieldOf("saturation").orElse(1.0f).forGetter(HoneyBottleData::saturation),
                HoneyEffect.CODEC.listOf().fieldOf("effects").orElse(Collections.emptyList()).forGetter(HoneyBottleData::effects),
                ForgeRegistries.ITEMS.getCodec().fieldOf("honeyBottle").orElse(Items.HONEY_BOTTLE).forGetter(HoneyBottleData::honeyBottle)
        ).apply(instance, HoneyBottleData::new));
    }

    public HoneyBottleData {
        if (HoneyRegistry.getRegistry().canGenerate()) {
            ModItems.HONEY_BOTTLE_ITEMS.register(name + "_honey_bottle", () -> new CustomHoneyBottleItem(this));
        }
    }

    public boolean hasEffects() {
        return effects != null && !effects.isEmpty();
    }

    public Item.Properties getProperties() {
        return new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HONEY).craftRemainder(Items.GLASS_BOTTLE).stacksTo(16);
    }

    public FoodProperties getFood() {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(hunger).saturationMod(saturation);
        if (hasEffects())
            effects.forEach(honeyEffect -> builder.effect(honeyEffect::getInstance, honeyEffect.chance()));
        return builder.build();
    }
}
