package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

public final class ModFluids {

    public static final ResourcefulRegistry<Fluid> FLUIDS = ResourcefulRegistries.create(BuiltInRegistries.FLUID, ModConstants.MOD_ID);
    public static final ResourcefulRegistry<Fluid> STILL_HONEY_FLUIDS = ResourcefulRegistries.create(FLUIDS);
    public static final ResourcefulRegistry<Fluid> FLOWING_HONEY_FLUIDS = ResourcefulRegistries.create(FLUIDS);

    //public static final RegistryEntry<Fluid> HONEY_STILL = STILL_HONEY_FLUIDS.register("honey", () ->  new BotariumSourceFluid(ModFluidProperties.HONEY));
    //public static final RegistryEntry<Fluid> HONEY_FLOWING = FLOWING_HONEY_FLUIDS.register("honey_flowing", () ->  new BotariumFlowingFluid(ModFluidProperties.HONEY));
}