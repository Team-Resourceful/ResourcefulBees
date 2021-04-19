package com.resourcefulbees.resourcefulbees.api.honeydata;

import com.resourcefulbees.resourcefulbees.registry.ItemGroupResourcefulBees;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class HoneyBottleData {

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * optional: name of the bee
     */
    private String name = null;

    /**
     * hunger replenished when drunk
     */
    private int hunger = 1;

    /**
     * saturation replenished when drunk
     */
    private float saturation = 1.0f;

    /**
     * color of the honey fluid
     */
    private String honeyColor = "#FFFFFF";

    /**
     * optional: defines if honey is rainbow
     */
    private boolean isRainbow = false;

    /**
     * optional: defines if a honey block should be generated
     */
    private boolean generateHoneyBlock = true;

    /**
     * optional: defines if a honey block should be generated
     */
    private boolean honeyBlockRecipe = true;

    /**
     * optional: defines if honey fluid should be generated
     */
    private boolean generateHoneyFluid = true;

    /**
     * optional: list of effects given by drinking the item
     */
    private List<HoneyEffect> effects;

    /**
     * The RegistryObject of the Honey Bottle Item
     */
    private transient RegistryObject<Item> honeyBottleRegistryObject;

    /**
     * The RegistryObject of the Honey Block Item
     */
    private transient RegistryObject<Item> honeyBlockItemRegistryObject;

    /**
     * The RegistryObject of the Honey Block
     */
    private transient RegistryObject<Block> honeyBlockRegistryObject;

    /**
     * The RegistryObject of the Still Honey Fluid
     */
    private transient RegistryObject<FlowingFluid> honeyStillFluidRegistryObject;

    /**
     * The RegistryObject of the Flowing Honey Fluid
     */
    private transient RegistryObject<FlowingFluid> honeyFlowingFluidRegistryObject;

    /**
     * The RegistryObject of the Honey Bucket Item
     */
    private transient RegistryObject<Item> honeyBucketItemRegistryObject;

    /**
     * The RegistryObject of the Honey Flowing Block
     */
    private transient RegistryObject<LiquidBlock> honeyFluidBlockRegistryObject;

    private transient boolean shouldResourcefulBeesDoForgeRegistration;

    public HoneyBottleData() {
    }

    public HoneyBottleData(String name, int hunger, float saturation, String honeyColor, boolean isRainbow, boolean generateHoneyBlock, boolean generateHoneyFluid, boolean honeyBlockRecipe, List<HoneyEffect> effects) {
        this.name = name;
        this.hunger = hunger;
        this.saturation = saturation;
        this.honeyColor = honeyColor;
        this.isRainbow = isRainbow;
        this.generateHoneyBlock = generateHoneyBlock;
        this.generateHoneyFluid = generateHoneyFluid;
        this.honeyBlockRecipe = honeyBlockRecipe;
        this.effects = effects;
    }

    public int getHoneyColorInt() {
        return com.resourcefulbees.resourcefulbees.utils.color.Color.parseInt(honeyColor);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean hasHoneyColor() {
        return honeyColor != null && !honeyColor.isEmpty();
    }

    public String getHoneyColor() {
        return honeyColor == null ? "#ffffff" : honeyColor;
    }

    public int getHunger() {
        return hunger;
    }

    public float getSaturation() {
        return saturation;
    }

    public boolean isRainbow() {
        return isRainbow;
    }

    public List<HoneyEffect> getEffects() {
        return effects == null ? new ArrayList<>() : effects;
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

    public void setHoneyFluidBlockRegistryObject(RegistryObject<LiquidBlock> honeyFluidBlockRegistryObject) {
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

    public RegistryObject<LiquidBlock> getHoneyFluidBlockRegistryObject() {
        return honeyFluidBlockRegistryObject;
    }

    public Item.Properties getProperties() {
        return new Item.Properties()
                .tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES)
                .craftRemainder(Items.GLASS_BOTTLE)
                .stacksTo(16);
    }

    public FoodProperties getFood() {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(hunger).saturationMod(saturation);
        if (hasEffects()) {
            for (HoneyEffect honeyEffect : effects) {
                builder.effect(honeyEffect::getInstance, honeyEffect.chance);
            }
        }
        return builder.build();
    }

    private boolean hasEffects() {
        return effects != null && !effects.isEmpty();
    }

    public boolean doGenerateHoneyBlock() {
        return generateHoneyBlock;
    }

    public boolean doGenerateHoneyFluid() {
        return generateHoneyFluid;
    }

    public boolean doHoneyBlockRecipe() {
        return honeyBlockRecipe;
    }

    public void setEffects(List<HoneyEffect> effects) {
        this.effects = effects;
    }

    /**
     * If the ResourcefulBees mod should handle the registration
     */
    public boolean shouldResourcefulBeesDoForgeRegistration() {
        return shouldResourcefulBeesDoForgeRegistration;
    }

    public void setShouldResourcefulBeesDoForgeRegistration(boolean shouldResourcefulBeesDoForgeRegistration) {
        this.shouldResourcefulBeesDoForgeRegistration = shouldResourcefulBeesDoForgeRegistration;
    }

    public TranslatableComponent getFluidTranslation() {
        return new TranslatableComponent(String.format("fluid.resourcefulbees.%s_honey", name));
    }

    public TranslatableComponent getBottleTranslation() {
        return new TranslatableComponent(String.format("item.resourcefulbees.%s_honey_bottle", name));
    }

    public TranslatableComponent getBlockTranslation() {
        return new TranslatableComponent(String.format("block.resourcefulbees.%s_honey_block", name));
    }

    public TranslatableComponent getBucketTranslation() {
        return new TranslatableComponent(String.format("item.resourcefulbees.%s_honey_fluid_bucket", name));
    }

    public static class Builder {
        private String name;
        private int hunger;
        private float saturation;
        private String honeyColor;
        private boolean isRainbow = false;
        private boolean generateHoneyBlock = true;
        private boolean honeyBlockRecipe = true;
        private boolean generateHoneyFluid = true;
        private final List<HoneyEffect> effects = new ArrayList<>();

        public Builder(String name, int hunger, float saturation, String honeyColor) {
            this.name = name;
            this.hunger = hunger;
            this.saturation = saturation;
            this.honeyColor = honeyColor;
        }

        public Builder setRainbow(boolean rainbow) {
            isRainbow = rainbow;
            return this;
        }

        public Builder setGenerateHoneyBlock(boolean generateHoneyBlock) {
            this.generateHoneyBlock = generateHoneyBlock;
            return this;
        }

        public Builder setHoneyBlockRecipe(boolean honeyBlockRecipe) {
            this.honeyBlockRecipe = honeyBlockRecipe;
            return this;
        }

        public Builder setGenerateHoneyFluid(boolean generateHoneyFluid) {
            this.generateHoneyFluid = generateHoneyFluid;
            return this;
        }

        public Builder addEffect(HoneyEffect effect) {
            effects.add(effect);
            return this;
        }

        public HoneyBottleData build() {
            return new HoneyBottleData(name, hunger, saturation, honeyColor, isRainbow, generateHoneyBlock, generateHoneyFluid, honeyBlockRecipe, effects);
        }
    }
}
