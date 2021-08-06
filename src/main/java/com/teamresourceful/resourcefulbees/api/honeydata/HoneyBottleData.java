package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.registry.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HoneyBottleData {

    public static final Codec<HoneyBottleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("hunger").orElse(1).forGetter(HoneyBottleData::getHunger),
            Codec.FLOAT.fieldOf("saturation").orElse(1.0f).forGetter(HoneyBottleData::getSaturation),
            Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneyBottleData::getColor),
            Codec.BOOL.fieldOf("generateHoneyBlock").orElse(true).forGetter(HoneyBottleData::doGenerateHoneyBlock),
            Codec.BOOL.fieldOf("generateBlockRecipe").orElse(true).forGetter(HoneyBottleData::doGenerateHoneyBlock),
            Codec.BOOL.fieldOf("generateHoneyFluid").orElse(true).forGetter(HoneyBottleData::doGenerateHoneyFluid),
            HoneyEffect.CODEC.listOf().fieldOf("honeyEffects").orElse(Collections.emptyList()).forGetter(HoneyBottleData::getHoneyEffects)
    ).apply(instance, HoneyBottleData::new));

    public static final Logger LOGGER = LogManager.getLogger();


    private String name;
    private final int hunger;
    private final float saturation;
    private final Color color;
    private final boolean generateHoneyBlock;
    private final boolean generateBlockRecipe;
    private final boolean generateHoneyFluid;
    private final List<HoneyEffect> honeyEffects;

    public HoneyBottleData(int hunger, float saturation, Color color, boolean generateHoneyBlock, boolean generateBlockRecipe, boolean generateHoneyFluid, List<HoneyEffect> honeyEffects) {
        this.hunger = hunger;
        this.saturation = saturation;
        this.color = color;
        this.generateHoneyBlock = generateHoneyBlock;
        this.generateBlockRecipe = generateBlockRecipe;
        this.generateHoneyFluid = generateHoneyFluid;
        this.honeyEffects = honeyEffects;
    }

    /**
     * The RegistryObject of the Honey Bottle Item
     */
    private RegistryObject<Item> honeyBottleRegistryObject;

    /**
     * The RegistryObject of the Honey Block Item
     */
    private RegistryObject<Item> honeyBlockItemRegistryObject;

    /**
     * The RegistryObject of the Honey Block
     */
    private RegistryObject<Block> honeyBlockRegistryObject;

    /**
     * The RegistryObject of the Still Honey Fluid
     */
    private RegistryObject<FlowingFluid> honeyStillFluidRegistryObject;

    /**
     * The RegistryObject of the Flowing Honey Fluid
     */
    private RegistryObject<FlowingFluid> honeyFlowingFluidRegistryObject;

    /**
     * The RegistryObject of the Honey Bucket Item
     */
    private RegistryObject<Item> honeyBucketItemRegistryObject;

    /**
     * The RegistryObject of the Honey Flowing Block
     */
    private RegistryObject<FlowingFluidBlock> honeyFluidBlockRegistryObject;

    public HoneyBottleData setName(String name) {
        this.name = name;
        return this;
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

    public List<HoneyEffect> getHoneyEffects() {
        return honeyEffects == null ? new ArrayList<>() : honeyEffects;
    }

    public void setHoneyBottleRegistryObject(RegistryObject<Item> honeyBottleRegistryObject) {
        this.honeyBottleRegistryObject = this.honeyBottleRegistryObject == null ? honeyBottleRegistryObject : this.honeyBottleRegistryObject;
    }

    public void setHoneyBlockItemRegistryObject(RegistryObject<Item> honeyBlockItemRegistryObject) {
        this.honeyBlockItemRegistryObject = this.honeyBlockItemRegistryObject == null ? honeyBlockItemRegistryObject : this.honeyBlockItemRegistryObject;
    }

    public void setHoneyBlockRegistryObject(RegistryObject<Block> honeyBlockRegistryObject) {
        this.honeyBlockRegistryObject = this.honeyBlockRegistryObject == null ? honeyBlockRegistryObject : this.honeyBlockRegistryObject;
    }

    public void setHoneyStillFluidRegistryObject(RegistryObject<FlowingFluid> honeyStillFluidRegistryObject) {
        this.honeyStillFluidRegistryObject = this.honeyStillFluidRegistryObject == null ? honeyStillFluidRegistryObject : this.honeyStillFluidRegistryObject;
    }

    public void setHoneyFlowingFluidRegistryObject(RegistryObject<FlowingFluid> honeyFlowingFluidRegistryObject) {
        this.honeyFlowingFluidRegistryObject = this.honeyFlowingFluidRegistryObject == null ? honeyFlowingFluidRegistryObject : this.honeyFlowingFluidRegistryObject;
    }

    public void setHoneyBucketItemRegistryObject(RegistryObject<Item> honeyBucketItemRegistryObject) {
        this.honeyBucketItemRegistryObject = this.honeyBucketItemRegistryObject == null ? honeyBucketItemRegistryObject : this.honeyBucketItemRegistryObject;
    }

    public void setHoneyFluidBlockRegistryObject(RegistryObject<FlowingFluidBlock> honeyFluidBlockRegistryObject) {
        this.honeyFluidBlockRegistryObject = this.honeyFluidBlockRegistryObject == null ? honeyFluidBlockRegistryObject : this.honeyFluidBlockRegistryObject;
    }

    public RegistryObject<Item> getHoneyBottleRegistryObject() {
        return honeyBottleRegistryObject;
    }

    public RegistryObject<Item> getHoneyBlockItemRegistryObject() {
        return honeyBlockItemRegistryObject;
    }

    public RegistryObject<Block> getHoneyBlockRegistryObject() {
        return honeyBlockRegistryObject;
    }

    public RegistryObject<FlowingFluid> getHoneyStillFluidRegistryObject() {
        return honeyStillFluidRegistryObject;
    }

    public RegistryObject<FlowingFluid> getHoneyFlowingFluidRegistryObject() {
        return honeyFlowingFluidRegistryObject;
    }

    public RegistryObject<Item> getHoneyBucketItemRegistryObject() {
        return honeyBucketItemRegistryObject;
    }

    public RegistryObject<FlowingFluidBlock> getHoneyFluidBlockRegistryObject() {
        return honeyFluidBlockRegistryObject;
    }

    public Item.Properties getProperties() {
        return new Item.Properties()
                .tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES)
                .craftRemainder(Items.GLASS_BOTTLE)
                .stacksTo(16);
    }

    public Food getFood() {
        Food.Builder builder = new Food.Builder().nutrition(hunger).saturationMod(saturation);
        if (hasEffects()) {
            for (HoneyEffect honeyEffect : honeyEffects) {
                builder.effect(honeyEffect::getInstance, honeyEffect.getChance());
            }
        }
        return builder.build();
    }

    private boolean hasEffects() {
        return honeyEffects != null && !honeyEffects.isEmpty();
    }

    public boolean doGenerateHoneyBlock() {
        return generateHoneyBlock;
    }

    public boolean doGenerateHoneyFluid() {
        return generateHoneyFluid;
    }

    public boolean doHoneyBlockRecipe() {
        return generateBlockRecipe;
    }


    public TranslationTextComponent getFluidTranslation() {
        return new TranslationTextComponent(String.format("fluid.resourcefulbees.%s_honey", name));
    }

    public TranslationTextComponent getBottleTranslation() {
        return new TranslationTextComponent(String.format("item.resourcefulbees.%s_honey_bottle", name));
    }

    public TranslationTextComponent getBlockTranslation() {
        return new TranslationTextComponent(String.format("block.resourcefulbees.%s_honey_block", name));
    }

    public TranslationTextComponent getBucketTranslation() {
        return new TranslationTextComponent(String.format("item.resourcefulbees.%s_honey_fluid_bucket", name));
    }
}
