package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.Collections;
import java.util.List;

public class HoneyBottleData {

    public static Codec<HoneyBottleData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(HoneyBottleData::getName),
                Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneyBottleData::getColor),
                Codec.INT.fieldOf("hunger").orElse(1).forGetter(HoneyBottleData::getHunger),
                Codec.FLOAT.fieldOf("saturation").orElse(1.0f).forGetter(HoneyBottleData::getSaturation),
                HoneyEffect.CODEC.listOf().fieldOf("effects").orElse(Collections.emptyList()).forGetter(HoneyBottleData::getEffects)
        ).apply(instance, HoneyBottleData::new));
    }

    private final String name;
    private final Color color;
    private final int hunger;
    private final float saturation;
    private final List<HoneyEffect> effects;
    private final RegistryObject<Item> honeyBottle;

    public HoneyBottleData(String name, Color color, int hunger, float saturation, List<HoneyEffect> effects){
        this.name = name;
        this.color = color;
        this.hunger = hunger;
        this.saturation = saturation;
        this.effects = effects;

        honeyBottle = ModItems.HONEY_BOTTLE_ITEMS.register(name + "_honey_bottle", () -> new CustomHoneyBottleItem(this));
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getHunger() {
        return hunger;
    }

    public float getSaturation() {
        return saturation;
    }

    public List<HoneyEffect> getEffects() {
        return effects;
    }

    public boolean hasEffects() {
        return effects != null && !effects.isEmpty();
    }

    public RegistryObject<Item> getHoneyBottle() {
        return honeyBottle;
    }

    public Item.Properties getProperties() {
        return new Item.Properties()
                .tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HONEY)
                .craftRemainder(Items.GLASS_BOTTLE)
                .stacksTo(16);
    }

    public Food getFood() {
        Food.Builder builder = new Food.Builder().nutrition(hunger).saturationMod(saturation);
        if (hasEffects()) effects.forEach(honeyEffect -> builder.effect(honeyEffect::getInstance, honeyEffect.getChance()));
        return builder.build();
    }
}
