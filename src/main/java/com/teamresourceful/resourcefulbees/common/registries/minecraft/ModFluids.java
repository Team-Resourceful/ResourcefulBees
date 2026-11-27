package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.fluids.HoneyFluidType;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.compat.NamespacedFluidRegistry;
import com.teamresourceful.resourcefulbees.common.registries.compat.NamespacedRegistry;
import com.teamresourceful.resourcefulbees.common.registries.compat.NamespacedRegistryChild;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.fluid.ResourcefulFlowingFluid;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

public final class ModFluids {
    private ModFluids() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final NamespacedRegistry<Fluid> FLUIDS = new NamespacedRegistry<>(
            ResourcefulRegistries.create(
                    BuiltInRegistries.FLUID,
                    ModConstants.MOD_ID
            ),
            namespace -> ResourcefulRegistries.create(
                    BuiltInRegistries.FLUID,
                    namespace
            )
    );
    public static final NamespacedRegistryChild<Fluid> STILL_HONEY_FLUIDS = new NamespacedRegistryChild<>(FLUIDS);
    public static final NamespacedRegistryChild<Fluid> FLOWING_HONEY_FLUIDS = new NamespacedRegistryChild<>(FLUIDS);
    public static final NamespacedFluidRegistry FLUID_TYPES = new NamespacedFluidRegistry(ModConstants.MOD_ID);

    public static final RegistryEntry<FluidData> HONEY_FLUID_TYPE = FLUID_TYPES.register("honey", HoneyFluidType.create());

    public static final RegistryEntry<Fluid> HONEY_STILL = STILL_HONEY_FLUIDS.register("honey_fluid_source", () ->  new ResourcefulFlowingFluid.Still(HONEY_FLUID_TYPE.get()));
    public static final RegistryEntry<Fluid> HONEY_FLOWING = FLOWING_HONEY_FLUIDS.register("honey_fluid_flowing", () ->  new ResourcefulFlowingFluid.Flowing(HONEY_FLUID_TYPE.get()));
}