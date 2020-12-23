package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ResourcefulBees.MOD_ID);

    public static final ResourceLocation FLUID_STILL = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_still");
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_flow");
    public static final ResourceLocation FLUID_OVERLAY = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_overlay");

    public static final ResourceLocation CUSTOM_FLUID_STILL = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_still");
    public static final ResourceLocation CUSTOM_FLUID_FLOWING = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_flow");
    public static final ResourceLocation CUSTOM_FLUID_OVERLAY = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_overlay");

    private static ForgeFlowingFluid.Properties makeProperties() {
        return new ForgeFlowingFluid.Properties(HONEY_STILL, HONEY_FLOWING,
                FluidAttributes.builder(FLUID_STILL, FLUID_FLOWING).sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
                        .overlay(FLUID_OVERLAY).density(1300).temperature(300).viscosity(1800).rarity(Rarity.COMMON))
                .bucket(ModItems.HONEY_FLUID_BUCKET).block(ModBlocks.HONEY_FLUID_BLOCK).tickRate(20);
    }

    public static ForgeFlowingFluid.Properties makeCustomProperties(final Supplier<? extends Fluid> still, final Supplier<? extends Fluid> flowing, final Supplier<? extends Item> bucket, final Supplier<? extends FlowingFluidBlock> fluidBlock) {
        return new ForgeFlowingFluid.Properties(still, flowing, FluidAttributes.builder(CUSTOM_FLUID_STILL, CUSTOM_FLUID_FLOWING)
                .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
                .overlay(CUSTOM_FLUID_OVERLAY).density(1300).temperature(300).viscosity(1800).rarity(Rarity.COMMON))
                .bucket(bucket).block(fluidBlock).tickRate(20);
    }

    public static RegistryObject<FlowingFluid> HONEY_STILL = FLUIDS.register("honey", () ->
            new ForgeFlowingFluid.Source(makeProperties())
    );
    public static RegistryObject<FlowingFluid> HONEY_FLOWING = FLUIDS.register("honey_flowing", () ->
            new ForgeFlowingFluid.Flowing(makeProperties())
    );
}
