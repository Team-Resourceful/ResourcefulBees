package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.fluids.HoneyClientFluidProperties;
import com.teamresourceful.resourcefulbees.common.fluids.HoneyFluidType;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.client.fluid.data.ClientFluidProperties;
import com.teamresourceful.resourcefullib.client.registry.ResourcefulClientRegistries;
import com.teamresourceful.resourcefullib.client.registry.ResourcefulClientRegistryType;
import com.teamresourceful.resourcefullib.common.fluid.ResourcefulFlowingFluid;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import com.teamresourceful.resourcefullib.common.fluid.registry.ResourcefulFluidRegistry;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistryType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

public final class ModFluids {

    public static final ResourcefulRegistry<Fluid> FLUIDS = ResourcefulRegistries.create(BuiltInRegistries.FLUID, ModConstants.MOD_ID);
    public static final ResourcefulRegistry<Fluid> STILL_HONEY_FLUIDS = ResourcefulRegistries.create(FLUIDS);
    public static final ResourcefulRegistry<Fluid> FLOWING_HONEY_FLUIDS = ResourcefulRegistries.create(FLUIDS);
    public static final ResourcefulFluidRegistry FLUID_TYPES = ResourcefulRegistries.create(ResourcefulRegistryType.FLUID, ResourcefulBees.MODID);
    public static final ResourcefulRegistry<ClientFluidProperties> CLIENT_FLUID_PROPERTIES = ResourcefulClientRegistries.create(ResourcefulClientRegistryType.FLUID, ResourcefulBees.MODID);

    public static final RegistryEntry<FluidData> HONEY_FLUID_TYPE = FLUID_TYPES.register("honey", HoneyFluidType.create());

    public static final RegistryEntry<Fluid> HONEY_STILL = STILL_HONEY_FLUIDS.register("honey_fluid_source", () ->  new ResourcefulFlowingFluid.Still(HONEY_FLUID_TYPE.get()));
    public static final RegistryEntry<Fluid> HONEY_FLOWING = FLOWING_HONEY_FLUIDS.register("honey_fluid_flowing", () ->  new ResourcefulFlowingFluid.Flowing(HONEY_FLUID_TYPE.get()));
    public static final RegistryEntry<ClientFluidProperties> HONEY_CLIENT_PROPERTIES = CLIENT_FLUID_PROPERTIES.register("honey", HoneyClientFluidProperties::create);
}