package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.registry.ItemGroupResourcefulBees;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private transient RegistryObject<FlowingFluidBlock> honeyFluidBlockRegistryObject;

    /**
     * If the ResourcefulBees mod should handle the registration
     */
    public transient boolean shouldResourcefulBeesDoForgeRegistration;


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
        return effects;
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
                .group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)
                .containerItem(Items.GLASS_BOTTLE)
                .maxStackSize(16);
    }

    public Food getFood() {
        Food.Builder builder = new Food.Builder().hunger(hunger).saturation(saturation);

        if (hasEffects()) {
            for (HoneyEffect honeyEffect : effects) {
                if (honeyEffect.getEffect() == null) {
                    LOGGER.warn(String.format("An effect for: $s could not be properly validated and was skipped.", honeyBottleRegistryObject.getId().toString()));
                    continue;
                }
                builder.effect(() -> honeyEffect.getInstance(), honeyEffect.chance);
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


    /**
     * effect : generated from the effect id
     * instance : generated from the effect, duration and strength
     * effectID : used to define the effect used
     * duration : duration in ticks of the effect
     * strength : strength of the potion effect
     * chance : chance for effect to proc on drinking honey
     */
    public class HoneyEffect {
        public String effectID;
        public int duration = 60;
        public int strength = 0;
        public float chance = 1;

        public EffectInstance getInstance() {
            return new EffectInstance(getEffect(), duration, strength);
        }

        public boolean isEffectIDValid() {
            return effectID != null && effectID.matches("([(a-z)(0-9)_-]*|[(a-z)(0-9)_-]*:[(a-z)(0-9)_-]*)");
        }

        public Effect getEffect() {
            ResourceLocation location = ResourceLocation.tryCreate(effectID);
            return effectID == null || location == null ? null : ForgeRegistries.POTIONS.getValue(location);
        }

        public String getEffectID() {
            return effectID;
        }
    }
}
