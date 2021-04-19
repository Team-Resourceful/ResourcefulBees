package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFluids {

    private ModFluids() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ResourcefulBees.MOD_ID);

    public static final ResourceLocation FLUID_STILL = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_still");
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_flow");
    public static final ResourceLocation FLUID_OVERLAY = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_overlay");

    public static final ResourceLocation CATNIP_FLUID_STILL = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/catnip_honey_still");
    public static final ResourceLocation CATNIP_FLUID_FLOWING = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/catnip_honey_flow");
    public static final ResourceLocation CATNIP_FLUID_OVERLAY = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/catnip_honey_overlay");

    public static final ResourceLocation CUSTOM_FLUID_STILL = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_still");
    public static final ResourceLocation CUSTOM_FLUID_FLOWING = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_flow");
    public static final ResourceLocation CUSTOM_FLUID_OVERLAY = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/custom_honey_overlay");

    private static ForgeFlowingFluid.Properties makeProperties() {
        return new ForgeFlowingFluid.Properties(HONEY_STILL, HONEY_FLOWING,
                FluidAttributes.builder(FLUID_STILL, FLUID_FLOWING).sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
                        .overlay(FLUID_OVERLAY).density(1300).temperature(300).viscosity(1800).rarity(Rarity.COMMON))
                .bucket(ModItems.HONEY_FLUID_BUCKET).block(ModBlocks.HONEY_FLUID_BLOCK).tickRate(20);
    }

    private static ForgeFlowingFluid.Properties makeCatnipProperties() {
        return new ForgeFlowingFluid.Properties(CATNIP_HONEY_STILL, CATNIP_HONEY_FLOWING,
                FluidAttributes.builder(CATNIP_FLUID_STILL, CATNIP_FLUID_FLOWING).sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
                        .overlay(CATNIP_FLUID_OVERLAY).density(1300).temperature(300).viscosity(1800).rarity(Rarity.COMMON))
                .bucket(ModItems.CATNIP_HONEY_FLUID_BUCKET).block(ModBlocks.CATNIP_HONEY_FLUID_BLOCK).tickRate(20);
    }

    public static final RegistryObject<FlowingFluid> HONEY_STILL = FLUIDS.register("honey", () ->
            new ForgeFlowingFluid.Source(makeProperties())
    );
    public static final RegistryObject<FlowingFluid> HONEY_FLOWING = FLUIDS.register("honey_flowing", () ->
            new ForgeFlowingFluid.Flowing(makeProperties())
    );

    public static final RegistryObject<FlowingFluid> CATNIP_HONEY_STILL = FLUIDS.register("catnip_honey", () ->
            new ForgeFlowingFluid.Source(makeCatnipProperties())
    );

    public static final RegistryObject<FlowingFluid> CATNIP_HONEY_FLOWING = FLUIDS.register("catnip_honey_flowing", () ->
            new ForgeFlowingFluid.Flowing(makeCatnipProperties())
    );
}
