package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyFluidBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.fluids.HoneyFluidAttributes;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBucketItem;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

public class HoneyFluidData {

    public static final ResourceLocation CUSTOM_FLUID_STILL = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_still");
    public static final ResourceLocation CUSTOM_FLUID_FLOWING = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_flow");
    public static final ResourceLocation CUSTOM_FLUID_OVERLAY = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_overlay");

    public static HoneyFluidData getDefault(String name) {
        return new HoneyFluidData(false, name, Color.DEFAULT, CUSTOM_FLUID_STILL, CUSTOM_FLUID_FLOWING, CUSTOM_FLUID_OVERLAY, SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY, 1300, 300, 1800);
    }

    public static Codec<HoneyFluidData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> true)).forGetter(honeyFluidData -> true),
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(HoneyFluidData::getName),
                Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneyFluidData::getColor),
                ResourceLocation.CODEC.fieldOf("stillTexture").orElse(CUSTOM_FLUID_STILL).forGetter(HoneyFluidData::getStillTexture),
                ResourceLocation.CODEC.fieldOf("flowingTexture").orElse(CUSTOM_FLUID_FLOWING).forGetter(HoneyFluidData::getFlowingTexture),
                ResourceLocation.CODEC.fieldOf("overlayTexture").orElse(CUSTOM_FLUID_OVERLAY).forGetter(HoneyFluidData::getOverlayTexture),
                Registry.SOUND_EVENT.byNameCodec().fieldOf("pickupSound").orElse(SoundEvents.BUCKET_FILL).forGetter(h -> h.pickupSound),
                Registry.SOUND_EVENT.byNameCodec().fieldOf("emptySound").orElse(SoundEvents.BUCKET_EMPTY).forGetter(h -> h.emptySound),
                Codec.INT.fieldOf("density").orElse(1300).forGetter(h -> h.density),
                Codec.INT.fieldOf("temperature").orElse(300).forGetter(h -> h.temperature),
                Codec.INT.fieldOf("viscosity").orElse(1800).forGetter(h -> h.viscosity)
        ).apply(instance, HoneyFluidData::new));
    }

    private final String name;
    private final Color color;
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final ResourceLocation overlayTexture;
    //These are only used in the constructor but are given values here to make it still work for generating a codec using a
    //HoneyFluidData object.
    private final SoundEvent pickupSound;
    private final SoundEvent emptySound;
    private final int density;
    private final int temperature;
    private final int viscosity;

    private RegistryObject<FlowingFluid> stillFluid;
    private RegistryObject<FlowingFluid> flowingFluid;
    private RegistryObject<Item> fluidBucket;
    private RegistryObject<LiquidBlock> fluidBlock;

    public HoneyFluidData(boolean generate, String name, Color color, ResourceLocation stillTexture, ResourceLocation flowingTexture, ResourceLocation overlayTexture, SoundEvent pickupSound, SoundEvent emptySound, int density, int temperature, int viscosity){
        this.name = name;
        this.color = color;
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.pickupSound = pickupSound;
        this.emptySound = emptySound;
        this.density = density;
        this.temperature = temperature;
        this.viscosity = viscosity;

        if (Boolean.TRUE.equals(CommonConfig.HONEY_GENERATE_FLUIDS.get()) && generate) {
            FluidAttributes.Builder builder = HoneyFluidAttributes.builder(this.stillTexture, this.flowingTexture, this)
                    .overlay(this.overlayTexture)
                    .sound(this.pickupSound, this.emptySound)
                    .density(this.density)
                    .temperature(this.temperature)
                    .viscosity(this.viscosity);

            ForgeFlowingFluid.Properties[] properties = {null};

            stillFluid = ModFluids.STILL_HONEY_FLUIDS.register(name + "_honey", () -> new CustomHoneyFluid.Source(properties[0], this));
            flowingFluid = ModFluids.FLOWING_HONEY_FLUIDS.register(name + "_honey_flowing", () -> new CustomHoneyFluid.Flowing(properties[0], this));
            fluidBucket = ModItems.HONEY_BUCKET_ITEMS.register(name + "_honey_bucket", () -> new CustomHoneyBucketItem(this.stillFluid, new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HONEY).craftRemainder(Items.BUCKET).stacksTo(1), this));
            fluidBlock = ModBlocks.HONEY_FLUID_BLOCKS.register(name + "_honey", () -> new CustomHoneyFluidBlock(this.stillFluid, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops(), this));

            properties[0] = new ForgeFlowingFluid.Properties(this.stillFluid, this.flowingFluid, builder)
                    .bucket(this.fluidBucket)
                    .block(this.fluidBlock)
                    .tickRate(20);
        }
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    public ResourceLocation getFlowingTexture() {
        return flowingTexture;
    }

    public ResourceLocation getOverlayTexture() {
        return overlayTexture;
    }

    public RegistryObject<FlowingFluid> getStillFluid() {
        return stillFluid;
    }

    public RegistryObject<FlowingFluid> getFlowingFluid() {
        return flowingFluid;
    }

    public RegistryObject<Item> getFluidBucket() {
        return fluidBucket;
    }

    public RegistryObject<LiquidBlock> getFluidBlock() {
        return fluidBlock;
    }
}
