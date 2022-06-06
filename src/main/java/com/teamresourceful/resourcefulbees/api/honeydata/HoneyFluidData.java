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
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public record HoneyFluidData(boolean generate, String name, Color color, ResourceLocation stillTexture, ResourceLocation flowingTexture, ResourceLocation overlayTexture, SoundEvent pickupSound, SoundEvent emptySound, int density, int temperature, int viscosity, Fluid stillFluid, Fluid flowingFluid, Item fluidBucket, Block fluidBlock) {

    public static final ResourceLocation CUSTOM_FLUID_STILL = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_still");
    public static final ResourceLocation CUSTOM_FLUID_FLOWING = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_flow");
    public static final ResourceLocation CUSTOM_FLUID_OVERLAY = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_overlay");

    public static HoneyFluidData getDefault(String name) {
        return new HoneyFluidData(false, name, Color.DEFAULT, CUSTOM_FLUID_STILL, CUSTOM_FLUID_FLOWING, CUSTOM_FLUID_OVERLAY, SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY, 1300, 300, 1800, Fluids.EMPTY, Fluids.EMPTY, Items.AIR, Blocks.AIR);
    }

    public static Codec<HoneyFluidData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> true)).forGetter(HoneyFluidData::generate),
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(HoneyFluidData::name),
                Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneyFluidData::color),
                ResourceLocation.CODEC.fieldOf("stillTexture").orElse(CUSTOM_FLUID_STILL).forGetter(HoneyFluidData::stillTexture),
                ResourceLocation.CODEC.fieldOf("flowingTexture").orElse(CUSTOM_FLUID_FLOWING).forGetter(HoneyFluidData::flowingTexture),
                ResourceLocation.CODEC.fieldOf("overlayTexture").orElse(CUSTOM_FLUID_OVERLAY).forGetter(HoneyFluidData::overlayTexture),
                ForgeRegistries.SOUND_EVENTS.getCodec().fieldOf("pickupSound").orElse(SoundEvents.BUCKET_FILL).forGetter(HoneyFluidData::pickupSound),
                ForgeRegistries.SOUND_EVENTS.getCodec().fieldOf("emptySound").orElse(SoundEvents.BUCKET_EMPTY).forGetter(HoneyFluidData::emptySound),
                Codec.INT.fieldOf("density").orElse(1300).forGetter(HoneyFluidData::density),
                Codec.INT.fieldOf("temperature").orElse(300).forGetter(HoneyFluidData::temperature),
                Codec.INT.fieldOf("viscosity").orElse(1800).forGetter(HoneyFluidData::viscosity),
                ForgeRegistries.FLUIDS.getCodec().fieldOf("stillFluid").forGetter(HoneyFluidData::stillFluid),
                ForgeRegistries.FLUIDS.getCodec().fieldOf("flowingFluid").forGetter(HoneyFluidData::flowingFluid),
                ForgeRegistries.ITEMS.getCodec().fieldOf("fluidBucket").forGetter(HoneyFluidData::fluidBucket),
                ForgeRegistries.BLOCKS.getCodec().fieldOf("fluidBlock").forGetter(HoneyFluidData::fluidBlock)
        ).apply(instance, HoneyFluidData::new));
    }

    public HoneyFluidData {
        if (Boolean.TRUE.equals(CommonConfig.HONEY_GENERATE_FLUIDS.get()) && generate && HoneyRegistry.getRegistry().canGenerate()) {
            FluidAttributes.Builder builder = HoneyFluidAttributes.builder(stillTexture, flowingTexture, this)
                    .overlay(overlayTexture)
                    .sound(pickupSound, emptySound)
                    .density(density)
                    .temperature(temperature)
                    .viscosity(viscosity);

            ForgeFlowingFluid.Properties[] properties = {null};

            RegistryObject<FlowingFluid> stillFluidRegistry = ModFluids.STILL_HONEY_FLUIDS.register(name + "_honey", () -> new CustomHoneyFluid.Source(properties[0], this));
            RegistryObject<FlowingFluid> flowingFluidRegistry = ModFluids.FLOWING_HONEY_FLUIDS.register(name + "_honey_flowing", () -> new CustomHoneyFluid.Flowing(properties[0], this));
            RegistryObject<Item> fluidBucketRegistry = ModItems.HONEY_BUCKET_ITEMS.register(name + "_honey_bucket", () -> new CustomHoneyBucketItem(stillFluidRegistry, new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HONEY).craftRemainder(Items.BUCKET).stacksTo(1), this));
            RegistryObject<LiquidBlock> blockFluidRegistry = ModBlocks.HONEY_FLUID_BLOCKS.register(name + "_honey", () -> new CustomHoneyFluidBlock(stillFluidRegistry, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops(), this));

            properties[0] = new ForgeFlowingFluid.Properties(stillFluidRegistry, flowingFluidRegistry, builder)
                    .bucket(fluidBucketRegistry)
                    .block(blockFluidRegistry)
                    .tickRate(20);
        }
    }
}
